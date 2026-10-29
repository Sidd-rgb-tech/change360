package org.change360.app.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.change360.app.data.local.dao.ChallengeDao
import org.change360.app.data.local.dao.ImpactLogDao
import org.change360.app.data.local.dao.UserDao
import org.change360.app.data.local.datastore.UserPreferencesDataSource
import org.change360.app.data.local.entity.ImpactLogEntity
import org.change360.app.data.mapper.toDomain
import org.change360.app.data.mapper.toEntity
import org.change360.app.domain.model.Challenge
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ChallengeStepResult
import org.change360.app.domain.repository.ChallengeRepository

/**
 * Concrete implementation of [ChallengeRepository] managing sustainability quests,
 * multi-step progress logging, and atomic impact ledger writes.
 *
 * Coordinates:
 * - [ChallengeDao] for reactive queries, filtering, and progression updates.
 * - [ImpactLogDao] for logging atomic CO2, water, and waste contributions upon quest completion.
 * - [UserDao] and [UserPreferencesDataSource] for awarding XP, advancing streaks, and updating Aura.
 */
class ChallengeRepositoryImpl(
    private val challengeDao: ChallengeDao,
    private val impactLogDao: ImpactLogDao,
    private val userDao: UserDao,
    private val preferencesDataSource: UserPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ChallengeRepository {

    override fun observeAllChallenges(): Flow<List<Challenge>> {
        return challengeDao.observeAllChallenges()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeChallengesByCategory(category: ChallengeCategory): Flow<List<Challenge>> {
        return if (category == ChallengeCategory.ALL) {
            observeAllChallenges()
        } else {
            challengeDao.observeChallengesByCategory(category.name)
                .map { list -> list.map { it.toDomain() } }
                .flowOn(ioDispatcher)
        }
    }

    override fun observeActiveChallenges(): Flow<List<Challenge>> {
        return challengeDao.observeActiveChallenges()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeCompletedChallenges(): Flow<List<Challenge>> {
        return challengeDao.observeCompletedChallenges()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeChallengeById(id: String): Flow<Challenge?> {
        return challengeDao.observeChallengeById(id)
            .map { it?.toDomain() }
            .flowOn(ioDispatcher)
    }

    override suspend fun getChallengeById(id: String): Challenge? = withContext(ioDispatcher) {
        challengeDao.getChallengeById(id)?.toDomain()
    }

    override fun observeCompletedCount(): Flow<Int> {
        return challengeDao.observeCompletedCount()
            .flowOn(ioDispatcher)
    }

    override fun observeTotalCount(): Flow<Int> {
        return challengeDao.observeTotalCount()
            .flowOn(ioDispatcher)
    }

    override suspend fun logStep(challengeId: String): Result<ChallengeStepResult> = withContext(ioDispatcher) {
        runCatching {
            val challenge = challengeDao.getChallengeById(challengeId)
                ?: throw NoSuchElementException("Challenge with ID '$challengeId' not found.")

            if (challenge.isCompleted) {
                return@runCatching ChallengeStepResult(
                    updatedChallenge = challenge.toDomain(),
                    isJustCompleted = false,
                    xpAwarded = 0,
                    co2SavedKg = 0f,
                    waterSavedLiters = 0f,
                    wasteSavedKg = 0f
                )
            }

            val nextSteps = (challenge.completedSteps + 1).coerceAtMost(challenge.totalSteps)
            val isJustCompleted = nextSteps >= challenge.totalSteps
            val now = System.currentTimeMillis()
            val completedTimestamp = if (isJustCompleted) now else null

            // 1. Update challenge progress state in Room
            challengeDao.updateProgress(
                id = challengeId,
                completedSteps = nextSteps,
                isCompleted = isJustCompleted,
                completedAtEpochMillis = completedTimestamp
            )

            var awardedXp = 0
            var co2Saved = 0f
            var waterSaved = 0f
            var wasteSaved = 0f

            // 2. When completed, execute atomic impact log write & award XP/streak
            if (isJustCompleted) {
                awardedXp = challenge.xpReward
                co2Saved = challenge.co2SavedKg
                waterSaved = challenge.waterSavedLiters
                wasteSaved = challenge.wasteSavedKg

                val activeUserId = preferencesDataSource.activeUserId.first()
                    ?: userDao.getActiveUser()?.id
                    ?: "practitioner_active"

                // Write immutable impact ledger record
                val impactLog = ImpactLogEntity(
                    userId = activeUserId,
                    sourceChallengeId = challenge.id,
                    actionTitle = challenge.title,
                    co2SavedKg = co2Saved,
                    waterSavedLiters = waterSaved,
                    wasteSavedKg = wasteSaved,
                    loggedAtEpochMillis = now
                )
                impactLogDao.insertImpactLog(impactLog)

                // Advance user XP, calculate new Aura level, increment streak
                userDao.incrementXpAndStreak(
                    userId = activeUserId,
                    xpEarned = awardedXp,
                    streakIncrement = 1
                )

                // Record current epoch day for streak tracking
                val currentEpochDay = now / (24L * 60 * 60 * 1000L)
                preferencesDataSource.setLastActiveEpochDay(currentEpochDay)
            }

            // Retrieve fresh state from database
            val updated = challengeDao.getChallengeById(challengeId)
                ?: challenge.copy(
                    completedSteps = nextSteps,
                    isCompleted = isJustCompleted,
                    completedAtEpochMillis = completedTimestamp
                )

            ChallengeStepResult(
                updatedChallenge = updated.toDomain(),
                isJustCompleted = isJustCompleted,
                xpAwarded = awardedXp,
                co2SavedKg = co2Saved,
                waterSavedLiters = waterSaved,
                wasteSavedKg = wasteSaved
            )
        }
    }

    override suspend fun resetChallenge(challengeId: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val challenge = challengeDao.getChallengeById(challengeId)
                ?: throw NoSuchElementException("Challenge with ID '$challengeId' not found.")

            challengeDao.updateProgress(
                id = challenge.id,
                completedSteps = 0,
                isCompleted = false,
                completedAtEpochMillis = null
            )
        }
    }

    override suspend fun upsertChallenge(challenge: Challenge): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            challengeDao.upsertChallenge(challenge.toEntity())
        }
    }

    override suspend fun upsertChallenges(challenges: List<Challenge>): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            challengeDao.upsertChallenges(challenges.map { it.toEntity() })
        }
    }
}

