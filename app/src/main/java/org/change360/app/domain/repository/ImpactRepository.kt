package org.change360.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.change360.app.domain.model.ImpactLog
import org.change360.app.domain.model.ImpactSummary
import org.change360.app.domain.model.TimeRange

/**
 * Domain contract managing recorded environmental impact logs, time-series metrics,
 * and planetary conservation aggregations.
 */
interface ImpactRepository {

    /**
     * Observes all logged environmental actions in reverse-chronological order.
     */
    fun observeAllLogs(): Flow<List<ImpactLog>>

    /**
     * Observes impact logs for a specific user.
     */
    fun observeLogsForUser(userId: String): Flow<List<ImpactLog>>

    /**
     * Observes impact logs recorded on or after a specified epoch timestamp.
     */
    fun observeLogsSince(sinceEpochMillis: Long): Flow<List<ImpactLog>>

    /**
     * Observes aggregated impact analytics computed for a specific time range window.
     */
    fun observeImpactSummary(timeRange: TimeRange): Flow<ImpactSummary>

    /**
     * Observes cumulative all-time CO2 savings in kilograms.
     */
    fun observeTotalCo2Saved(): Flow<Float>

    /**
     * Observes cumulative all-time water conserved in liters.
     */
    fun observeTotalWaterSaved(): Flow<Float>

    /**
     * Observes cumulative all-time solid waste diverted in kilograms.
     */
    fun observeTotalWasteSaved(): Flow<Float>

    /**
     * Records an atomic custom environmental impact action into the ledger.
     */
    suspend fun logImpact(
        actionTitle: String,
        co2SavedKg: Float,
        waterSavedLiters: Float,
        wasteSavedKg: Float,
        sourceChallengeId: String? = null
    ): Result<Long>

    /**
     * Deletes a specific impact log by its identifier.
     */
    suspend fun deleteLogById(id: Long): Result<Unit>

    /**
     * Clears all recorded impact logs.
     */
    suspend fun clearAllLogs(): Result<Unit>
}

