package org.change360.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.change360.app.data.local.datastore.UserPreferencesData
import org.change360.app.domain.model.User

/**
 * Domain contract governing authentication, session persistence,
 * and user profile progression.
 */
interface AuthRepository {

    /**
     * Observes the active authenticated user profile reactively.
     */
    fun observeActiveUser(): Flow<User?>

    /**
     * Observes whether a valid user session is authenticated.
     */
    fun observeIsAuthenticated(): Flow<Boolean>

    /**
     * Observes lightweight session preferences (theme, streak, active user ID).
     */
    fun observeUserPreferences(): Flow<UserPreferencesData>

    /**
     * Synchronously retrieves the current active user snapshot.
     */
    suspend fun getActiveUser(): User?

    /**
     * Retrieves a user by their unique identifier.
     */
    suspend fun getUserById(userId: String): User?

    /**
     * Authenticates an existing user by email address.
     */
    suspend fun signIn(email: String): Result<User>

    /**
     * Registers a new user with full credentials and eco-pledge.
     */
    suspend fun signUp(displayName: String, email: String, ecoPledge: String): Result<User>

    /**
     * Fast-tracks onboarding with a guest eco-pledge profile.
     */
    suspend fun signInAsGuest(displayName: String, ecoPledge: String): Result<User>

    /**
     * Updates profile details for an existing user.
     */
    suspend fun updateUser(user: User): Result<Unit>

    /**
     * Updates the user's personal eco-pledge declaration.
     */
    suspend fun updateEcoPledge(userId: String, ecoPledge: String): Result<Unit>

    /**
     * Updates the active visual Aura theme variant preference.
     */
    suspend fun setThemeAuraVariant(variant: String): Result<Unit>

    /**
     * Clears active session credentials and signs out.
     */
    suspend fun signOut(): Result<Unit>
}

