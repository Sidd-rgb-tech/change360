package org.change360.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.change360.app.domain.model.Challenge
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ChallengeStepResult

/**
 * Domain contract managing sustainability challenges, multi-step progression,
 * and completion rewards.
 */
interface ChallengeRepository {

    /**
     * Observes all challenges ordered by completion state and reward XP.
     */
    fun observeAllChallenges(): Flow<List<Challenge>>

    /**
     * Observes challenges filtered by a specific category.
     */
    fun observeChallengesByCategory(category: ChallengeCategory): Flow<List<Challenge>>

    /**
     * Observes active (in-progress) challenges prioritized by logged steps.
     */
    fun observeActiveChallenges(): Flow<List<Challenge>>

    /**
     * Observes successfully accomplished challenges.
     */
    fun observeCompletedChallenges(): Flow<List<Challenge>>

    /**
     * Observes a single challenge by its unique identifier.
     */
    fun observeChallengeById(id: String): Flow<Challenge?>

    /**
     * Synchronously retrieves a challenge snapshot.
     */
    suspend fun getChallengeById(id: String): Challenge?

    /**
     * Observes the count of completed challenges.
     */
    fun observeCompletedCount(): Flow<Int>

    /**
     * Observes the total number of challenges available.
     */
    fun observeTotalCount(): Flow<Int>

    /**
     * Atomically logs an incremental progress step on a challenge.
     * When completed, awards XP and writes an immutable ImpactLog entry.
     */
    suspend fun logStep(challengeId: String): Result<ChallengeStepResult>

    /**
     * Resets a challenge back to 0 steps and marks it pending.
     */
    suspend fun resetChallenge(challengeId: String): Result<Unit>

    /**
     * Inserts or updates a single challenge.
     */
    suspend fun upsertChallenge(challenge: Challenge): Result<Unit>

    /**
     * Inserts or updates multiple challenges.
     */
    suspend fun upsertChallenges(challenges: List<Challenge>): Result<Unit>
}

