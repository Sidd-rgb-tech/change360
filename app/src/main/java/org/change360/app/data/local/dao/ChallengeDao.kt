package org.change360.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.change360.app.data.local.entity.ChallengeEntity

/**
 * Data Access Object for sustainability challenges, task completion, and step progression.
 */
@Dao
interface ChallengeDao {

    /**
     * Observes all challenges ordered by pending completion status and reward magnitude.
     */
    @Query("SELECT * FROM challenges ORDER BY isCompleted ASC, xpReward DESC")
    fun observeAllChallenges(): Flow<List<ChallengeEntity>>

    /**
     * Observes challenges filtered by specific category (ENERGY, WATER, ZERO_WASTE, MOBILITY, COMMUNITY).
     */
    @Query("SELECT * FROM challenges WHERE category = :category ORDER BY isCompleted ASC, xpReward DESC")
    fun observeChallengesByCategory(category: String): Flow<List<ChallengeEntity>>

    /**
     * Observes challenges that have been successfully accomplished.
     */
    @Query("SELECT * FROM challenges WHERE isCompleted = 1 ORDER BY completedAtEpochMillis DESC")
    fun observeCompletedChallenges(): Flow<List<ChallengeEntity>>

    /**
     * Observes active (uncompleted) challenges prioritized by logged steps.
     */
    @Query("SELECT * FROM challenges WHERE isCompleted = 0 ORDER BY completedSteps DESC")
    fun observeActiveChallenges(): Flow<List<ChallengeEntity>>

    /**
     * Observes a single challenge by its unique identifier.
     */
    @Query("SELECT * FROM challenges WHERE id = :id LIMIT 1")
    fun observeChallengeById(id: String): Flow<ChallengeEntity?>

    /**
     * Retrieves a single challenge snapshot.
     */
    @Query("SELECT * FROM challenges WHERE id = :id LIMIT 1")
    suspend fun getChallengeById(id: String): ChallengeEntity?

    /**
     * Observes the count of completed challenges.
     */
    @Query("SELECT COUNT(*) FROM challenges WHERE isCompleted = 1")
    fun observeCompletedCount(): Flow<Int>

    /**
     * Observes the total number of challenges available.
     */
    @Query("SELECT COUNT(*) FROM challenges")
    fun observeTotalCount(): Flow<Int>

    /**
     * Inserts or updates a single challenge.
     */
    @Upsert
    suspend fun upsertChallenge(challenge: ChallengeEntity)

    /**
     * Inserts or updates a list of challenges.
     */
    @Upsert
    suspend fun upsertChallenges(challenges: List<ChallengeEntity>)

    /**
     * Inserts challenges with replacement strategy for initial database seeding.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    /**
     * Updates challenge step progression and marks completion when threshold is reached.
     */
    @Query("UPDATE challenges SET completedSteps = :completedSteps, isCompleted = :isCompleted, completedAtEpochMillis = :completedAtEpochMillis WHERE id = :id")
    suspend fun updateProgress(
        id: String,
        completedSteps: Int,
        isCompleted: Boolean,
        completedAtEpochMillis: Long?
    )

    /**
     * Updates full challenge details.
     */
    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    /**
     * Deletes a challenge by its identifier.
     */
    @Query("DELETE FROM challenges WHERE id = :id")
    suspend fun deleteChallengeById(id: String)

    /**
     * Clears all challenges from the local store.
     */
    @Query("DELETE FROM challenges")
    suspend fun clearAllChallenges()
}

