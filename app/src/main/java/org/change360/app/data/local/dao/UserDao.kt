package org.change360.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.change360.app.data.local.entity.UserEntity

/**
 * Data Access Object for sustainability user profiles and gamified progression.
 */
@Dao
interface UserDao {

    /**
     * Observes the primary authenticated user profile.
     */
    @Query("SELECT * FROM users LIMIT 1")
    fun observeActiveUser(): Flow<UserEntity?>

    /**
     * Observes a specific user profile by unique identifier.
     */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUserById(userId: String): Flow<UserEntity?>

    /**
     * Retrieves a specific user profile snapshot synchronously within a coroutine.
     */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    /**
     * Retrieves the primary active user snapshot synchronously within a coroutine.
     */
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getActiveUser(): UserEntity?

    /**
     * Inserts or updates the user profile record.
     */
    @Upsert
    suspend fun upsertUser(user: UserEntity)

    /**
     * Inserts the user record with replace strategy.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * Updates an existing user record.
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Atomically increments user XP, recalculates Aura level (XP / 500 + 1),
     * and advances daily streak count.
     */
    @Query("UPDATE users SET xpPoints = xpPoints + :xpEarned, auraLevel = ((xpPoints + :xpEarned) / 500) + 1, currentStreakDays = currentStreakDays + :streakIncrement WHERE id = :userId")
    suspend fun incrementXpAndStreak(userId: String, xpEarned: Int, streakIncrement: Int = 1)

    /**
     * Updates the current active streak count for a user.
     */
    @Query("UPDATE users SET currentStreakDays = :streakDays WHERE id = :userId")
    suspend fun updateStreak(userId: String, streakDays: Int)

    /**
     * Directly updates the user's Aura tier level.
     */
    @Query("UPDATE users SET auraLevel = :level WHERE id = :userId")
    suspend fun updateAuraLevel(userId: String, level: Int)

    /**
     * Removes a specific user by identifier.
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    /**
     * Clears all user records.
     */
    @Query("DELETE FROM users")
    suspend fun clearUsers()
}

