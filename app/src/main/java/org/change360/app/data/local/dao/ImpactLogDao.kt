package org.change360.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.change360.app.data.local.entity.ImpactLogEntity

/**
 * Data Access Object for recorded environmental impact ledger entries and aggregated analytics.
 */
@Dao
interface ImpactLogDao {

    /**
     * Observes the complete stream of logged environmental actions in reverse-chronological order.
     */
    @Query("SELECT * FROM impact_logs ORDER BY loggedAtEpochMillis DESC")
    fun observeAllLogs(): Flow<List<ImpactLogEntity>>

    /**
     * Observes impact logs for a specific user.
     */
    @Query("SELECT * FROM impact_logs WHERE userId = :userId ORDER BY loggedAtEpochMillis DESC")
    fun observeLogsForUser(userId: String): Flow<List<ImpactLogEntity>>

    /**
     * Observes impact logs recorded on or after a specified epoch timestamp.
     */
    @Query("SELECT * FROM impact_logs WHERE loggedAtEpochMillis >= :sinceEpochMillis ORDER BY loggedAtEpochMillis DESC")
    fun observeLogsSince(sinceEpochMillis: Long): Flow<List<ImpactLogEntity>>

    /**
     * Observes impact logs for a specific user within a time window.
     */
    @Query("SELECT * FROM impact_logs WHERE userId = :userId AND loggedAtEpochMillis >= :sinceEpochMillis ORDER BY loggedAtEpochMillis DESC")
    fun observeLogsForUserSince(userId: String, sinceEpochMillis: Long): Flow<List<ImpactLogEntity>>

    /**
     * Observes cumulative all-time CO2 savings in kilograms across all actions.
     */
    @Query("SELECT COALESCE(SUM(co2SavedKg), 0.0) FROM impact_logs")
    fun observeTotalCo2Saved(): Flow<Float>

    /**
     * Observes cumulative all-time water conserved in liters across all actions.
     */
    @Query("SELECT COALESCE(SUM(waterSavedLiters), 0.0) FROM impact_logs")
    fun observeTotalWaterSaved(): Flow<Float>

    /**
     * Observes cumulative all-time landfill waste diverted in kilograms.
     */
    @Query("SELECT COALESCE(SUM(wasteSavedKg), 0.0) FROM impact_logs")
    fun observeTotalWasteSaved(): Flow<Float>

    /**
     * Observes CO2 savings achieved within a specific recent epoch window.
     */
    @Query("SELECT COALESCE(SUM(co2SavedKg), 0.0) FROM impact_logs WHERE loggedAtEpochMillis >= :sinceEpochMillis")
    fun observeCo2SavedSince(sinceEpochMillis: Long): Flow<Float>

    /**
     * Observes water conservation achieved within a specific recent epoch window.
     */
    @Query("SELECT COALESCE(SUM(waterSavedLiters), 0.0) FROM impact_logs WHERE loggedAtEpochMillis >= :sinceEpochMillis")
    fun observeWaterSavedSince(sinceEpochMillis: Long): Flow<Float>

    /**
     * Observes waste diversion achieved within a specific recent epoch window.
     */
    @Query("SELECT COALESCE(SUM(wasteSavedKg), 0.0) FROM impact_logs WHERE loggedAtEpochMillis >= :sinceEpochMillis")
    fun observeWasteSavedSince(sinceEpochMillis: Long): Flow<Float>

    /**
     * Retrieves any impact log linked to a specific challenge ID.
     */
    @Query("SELECT * FROM impact_logs WHERE sourceChallengeId = :challengeId LIMIT 1")
    suspend fun getLogByChallengeId(challengeId: String): ImpactLogEntity?

    /**
     * Inserts an atomic impact log entry, returning the auto-generated row ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImpactLog(log: ImpactLogEntity): Long

    /**
     * Inserts multiple impact log records.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImpactLogs(logs: List<ImpactLogEntity>)

    /**
     * Deletes a specific log entry by primary key.
     */
    @Query("DELETE FROM impact_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    /**
     * Clears all impact logs.
     */
    @Query("DELETE FROM impact_logs")
    suspend fun clearAllLogs()
}

