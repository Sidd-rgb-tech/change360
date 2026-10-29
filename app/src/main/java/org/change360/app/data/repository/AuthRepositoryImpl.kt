package org.change360.app.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.change360.app.data.local.dao.UserDao
import org.change360.app.data.local.datastore.UserPreferencesData
import org.change360.app.data.local.datastore.UserPreferencesDataSource
import org.change360.app.data.local.entity.UserEntity
import org.change360.app.data.mapper.toDomain
import org.change360.app.data.mapper.toEntity
import org.change360.app.domain.model.User
import org.change360.app.domain.repository.AuthRepository
import java.util.UUID

/**
 * Concrete implementation of [AuthRepository] coordinating Room [UserDao]
 * and Jetpack DataStore [UserPreferencesDataSource].
 *
 * Responsibilities:
 * - Emits reactive session state by joining DataStore user keys with Room user profiles.
 * - Manages Sign-In, Sign-Up, and zero-friction Guest Eco-Pledge onboarding.
 * - Handles profile modifications, pledge affirmations, and theme selections.
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val preferencesDataSource: UserPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeActiveUser(): Flow<User?> {
        return preferencesDataSource.activeUserId
            .flatMapLatest { activeId ->
                if (activeId != null) {
                    userDao.observeUserById(activeId)
                } else {
                    userDao.observeActiveUser()
                }
            }
            .map { it?.toDomain() }
            .flowOn(ioDispatcher)
    }

    override fun observeIsAuthenticated(): Flow<Boolean> {
        return preferencesDataSource.isAuthenticated
            .flowOn(ioDispatcher)
    }

    override fun observeUserPreferences(): Flow<UserPreferencesData> {
        return preferencesDataSource.userPreferences
            .flowOn(ioDispatcher)
    }

    override suspend fun getActiveUser(): User? = withContext(ioDispatcher) {
        val activeId = preferencesDataSource.activeUserId.first()
        val userEntity = if (activeId != null) {
            userDao.getUserById(activeId) ?: userDao.getActiveUser()
        } else {
            userDao.getActiveUser()
        }
        userEntity?.toDomain()
    }

    override suspend fun getUserById(userId: String): User? = withContext(ioDispatcher) {
        userDao.getUserById(userId)?.toDomain()
    }

    override suspend fun signIn(email: String): Result<User> = withContext(ioDispatcher) {
        runCatching {
            val sanitizedEmail = email.trim()
            require(sanitizedEmail.isNotBlank()) { "Email cannot be blank." }
            require(android.util.Patterns.EMAIL_ADDRESS.matcher(sanitizedEmail).matches() || sanitizedEmail.contains("@")) {
                "Please provide a valid email address."
            }

            // Look up existing user profile in local store or retrieve active user
            val existingUser = userDao.getActiveUser()
            val userToAuthenticate = if (existingUser != null && existingUser.email.equals(sanitizedEmail, ignoreCase = true)) {
                existingUser
            } else {
                // Provision a local practitioner profile if first login on device
                val defaultName = sanitizedEmail.substringBefore("@")
                    .replace(".", " ")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                UserEntity(
                    id = UUID.randomUUID().toString(),
                    displayName = defaultName.ifBlank { "Climate Champion" },
                    email = sanitizedEmail,
                    ecoPledge = "Cut single-use plastics & reduce daily carbon footprint",
                    auraLevel = 1,
                    xpPoints = 100,
                    currentStreakDays = 1,
                    createdAtEpochMillis = System.currentTimeMillis()
                ).also { userDao.upsertUser(it) }
            }

            // Commit persistent session in DataStore
            preferencesDataSource.saveSession(
                userId = userToAuthenticate.id,
                isAuthenticated = true
            )

            userToAuthenticate.toDomain()
        }
    }

    override suspend fun signUp(
        displayName: String,
        email: String,
        ecoPledge: String
    ): Result<User> = withContext(ioDispatcher) {
        runCatching {
            val cleanName = displayName.trim()
            val cleanEmail = email.trim()
            val cleanPledge = ecoPledge.trim()

            require(cleanName.length >= 2) { "Display name must be at least 2 characters long." }
            require(cleanEmail.contains("@") && cleanEmail.length >= 5) { "Please provide a valid email address." }
            require(cleanPledge.isNotBlank()) { "Please select or write an eco-pledge." }

            val newUserId = UUID.randomUUID().toString()
            val userEntity = UserEntity(
                id = newUserId,
                displayName = cleanName,
                email = cleanEmail,
                ecoPledge = cleanPledge,
                auraLevel = 1,
                xpPoints = 150, // Welcome starter bonus
                currentStreakDays = 1,
                createdAtEpochMillis = System.currentTimeMillis()
            )

            userDao.upsertUser(userEntity)
            preferencesDataSource.saveSession(userId = newUserId, isAuthenticated = true)

            userEntity.toDomain()
        }
    }

    override suspend fun signInAsGuest(
        displayName: String,
        ecoPledge: String
    ): Result<User> = withContext(ioDispatcher) {
        runCatching {
            val cleanName = displayName.trim().ifBlank { "Earth Guardian" }
            val cleanPledge = ecoPledge.trim().ifBlank { "Every daily action ripples across our biosphere. Choose regeneration." }

            val guestId = "guest_${UUID.randomUUID().toString().take(8)}"
            val guestUser = UserEntity(
                id = guestId,
                displayName = cleanName,
                email = "guest@change360.local",
                ecoPledge = cleanPledge,
                auraLevel = 1,
                xpPoints = 75,
                currentStreakDays = 1,
                createdAtEpochMillis = System.currentTimeMillis()
            )

            userDao.upsertUser(guestUser)
            preferencesDataSource.saveSession(userId = guestId, isAuthenticated = true)

            guestUser.toDomain()
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            userDao.updateUser(user.toEntity())
        }
    }

    override suspend fun updateEcoPledge(userId: String, ecoPledge: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            require(ecoPledge.isNotBlank()) { "Eco pledge cannot be empty." }
            val existing = userDao.getUserById(userId)
                ?: throw NoSuchElementException("User with ID $userId not found.")
            val updated = existing.copy(ecoPledge = ecoPledge.trim())
            userDao.updateUser(updated)
        }
    }

    override suspend fun setThemeAuraVariant(variant: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            preferencesDataSource.setThemeAuraVariant(variant)
        }
    }

    override suspend fun signOut(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            preferencesDataSource.clearSession()
        }
    }
}

