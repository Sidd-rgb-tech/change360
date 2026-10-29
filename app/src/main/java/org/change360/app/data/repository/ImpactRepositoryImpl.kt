package org.change360.app.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.change360.app.data.local.dao.ImpactLogDao
import org.change360.app.data.local.datastore.UserPreferencesDataSource
import org.change360.app.data.local.entity.ImpactLogEntity
import org.change360.app.data.mapper.toDomain
import org.change360.app.domain.model.CategoryImpactBreakdown
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ImpactLog
import org.change360.app.domain.model.ImpactSummary
import org.change360.app.domain.model.TimeRange
import org.change360.app.domain.repository.ImpactRepository

/**
 * Concrete implementation of [ImpactRepository] aggregating time-series environmental metrics
 * from Room [ImpactLogDao] and session configuration from [UserPreferencesDataSource].
 *
 * Responsibilities:
 * - Aggregates time-series metrics across 7-day, 30-day, and all-time windows.
 * - Computes tangible planetary equivalents (trees absorbing CO2, car-kilometers avoided, plastic bottles diverted).
 * - Categorizes environmental contributions across Energy, Water, Zero-Waste, Mobility, and Community.
 * - Handles custom impact ledger entries and administrative log pruning.
 */
class ImpactRepositoryImpl(
    private val impactLogDao: ImpactLogDao,
    private val preferencesDataSource: UserPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ImpactRepository {

    override fun observeAllLogs(): Flow<List<ImpactLog>> {
        return impactLogDao.observeAllLogs()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeLogsForUser(userId: String): Flow<List<ImpactLog>> {
        return impactLogDao.observeLogsForUser(userId)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeLogsSince(sinceEpochMillis: Long): Flow<List<ImpactLog>> {
        return impactLogDao.observeLogsSince(sinceEpochMillis)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeImpactSummary(timeRange: TimeRange): Flow<ImpactSummary> {
        val flowOfEntities = if (timeRange == TimeRange.ALL_TIME) {
            impactLogDao.observeAllLogs()
        } else {
            impactLogDao.observeLogsSince(timeRange.getSinceTimestamp())
        }

        return flowOfEntities
            .map { entities ->
                val domainLogs = entities.map { it.toDomain() }
                buildImpactSummary(domainLogs, timeRange)
            }
            .flowOn(ioDispatcher)
    }

    override fun observeTotalCo2Saved(): Flow<Float> {
        return impactLogDao.observeTotalCo2Saved()
            .flowOn(ioDispatcher)
    }

    override fun observeTotalWaterSaved(): Flow<Float> {
        return impactLogDao.observeTotalWaterSaved()
            .flowOn(ioDispatcher)
    }

    override fun observeTotalWasteSaved(): Flow<Float> {
        return impactLogDao.observeTotalWasteSaved()
            .flowOn(ioDispatcher)
    }

    override suspend fun logImpact(
        actionTitle: String,
        co2SavedKg: Float,
        waterSavedLiters: Float,
        wasteSavedKg: Float,
        sourceChallengeId: String?
    ): Result<Long> = withContext(ioDispatcher) {
        runCatching {
            require(actionTitle.isNotBlank()) { "Action title cannot be blank." }
            require(co2SavedKg >= 0f) { "CO2 saved cannot be negative." }
            require(waterSavedLiters >= 0f) { "Water saved cannot be negative." }
            require(wasteSavedKg >= 0f) { "Waste saved cannot be negative." }

            val activeUserId = preferencesDataSource.activeUserId.first()
                ?: "practitioner_active"

            val entity = ImpactLogEntity(
                userId = activeUserId,
                sourceChallengeId = sourceChallengeId,
                actionTitle = actionTitle.trim(),
                co2SavedKg = co2SavedKg,
                waterSavedLiters = waterSavedLiters,
                wasteSavedKg = wasteSavedKg,
                loggedAtEpochMillis = System.currentTimeMillis()
            )

            impactLogDao.insertImpactLog(entity)
        }
    }

    override suspend fun deleteLogById(id: Long): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            impactLogDao.deleteLogById(id)
        }
    }

    override suspend fun clearAllLogs(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            impactLogDao.clearAllLogs()
        }
    }

    /**
     * Synthesizes raw impact entries into an aggregated [ImpactSummary]
     * with categorized distributions and tangible planetary equivalents.
     */
    private fun buildImpactSummary(
        logs: List<ImpactLog>,
        timeRange: TimeRange
    ): ImpactSummary {
        val totalCo2 = logs.sumOf { it.co2SavedKg.toDouble() }.toFloat()
        val totalWater = logs.sumOf { it.waterSavedLiters.toDouble() }.toFloat()
        val totalWaste = logs.sumOf { it.wasteSavedKg.toDouble() }.toFloat()

        // Real-world equivalency models
        val trees = if (totalCo2 > 0f) totalCo2 / ImpactSummary.CO2_KG_PER_TREE_YEAR else 0f
        val carKm = if (totalCo2 > 0f) totalCo2 * ImpactSummary.KM_PER_KG_CO2 else 0f
        val bottles = if (totalWaste > 0f) (totalWaste * ImpactSummary.BOTTLES_PER_KG_WASTE).toInt() else 0

        // Classify impact logs into categories
        val categoryGroups = logs.groupBy { deduceCategory(it) }

        val categoryBreakdowns = ChallengeCategory.entries
            .filter { it != ChallengeCategory.ALL }
            .map { cat ->
                val groupLogs = categoryGroups[cat] ?: emptyList()
                val catCo2 = groupLogs.sumOf { it.co2SavedKg.toDouble() }.toFloat()
                val catWater = groupLogs.sumOf { it.waterSavedLiters.toDouble() }.toFloat()
                val catWaste = groupLogs.sumOf { it.wasteSavedKg.toDouble() }.toFloat()
                val percentage = if (totalCo2 > 0f) (catCo2 / totalCo2) * 100f else 0f

                CategoryImpactBreakdown(
                    category = cat,
                    co2Kg = catCo2,
                    waterLiters = catWater,
                    wasteKg = catWaste,
                    actionsCount = groupLogs.size,
                    percentageOfTotalCo2 = percentage
                )
            }

        return ImpactSummary(
            totalCo2SavedKg = totalCo2,
            totalWaterSavedLiters = totalWater,
            totalWasteSavedKg = totalWaste,
            treesPlantedEquivalent = trees,
            carKmAvoided = carKm,
            plasticBottlesSaved = bottles,
            actionsLoggedCount = logs.size,
            timeRange = timeRange,
            categoryBreakdowns = categoryBreakdowns
        )
    }

    /**
     * Deduce the sustainability category from an [ImpactLog] action title and metrics.
     */
    private fun deduceCategory(log: ImpactLog): ChallengeCategory {
        val title = log.actionTitle.lowercase()
        val sourceId = log.sourceChallengeId?.lowercase() ?: ""

        return when {
            sourceId.startsWith("energy") || title.contains("power") || title.contains("electric") ||
                    title.contains("energy") || title.contains("phantom") || title.contains("wash") ||
                    title.contains("load") -> ChallengeCategory.ENERGY

            sourceId.startsWith("water") || title.contains("water") || title.contains("shower") ||
                    title.contains("rinse") || title.contains("greywater") || title.contains("leak") ||
                    title.contains("plumbing") || (log.waterSavedLiters > 0f && log.wasteSavedKg == 0f && log.co2SavedKg < 1.0f) -> ChallengeCategory.WATER

            sourceId.startsWith("waste") || title.contains("plastic") || title.contains("waste") ||
                    title.contains("compost") || title.contains("bottle") || title.contains("reusable") ||
                    title.contains("pantry") || (log.wasteSavedKg > 0f) -> ChallengeCategory.ZERO_WASTE

            sourceId.startsWith("mobility") || title.contains("transit") || title.contains("commute") ||
                    title.contains("bike") || title.contains("bicycle") || title.contains("car") ||
                    title.contains("metro") || title.contains("train") || title.contains("bus") -> ChallengeCategory.MOBILITY

            sourceId.startsWith("community") || title.contains("poster") || title.contains("advocacy") ||
                    title.contains("park") || title.contains("cleanup") || title.contains("sapling") ||
                    title.contains("tree") || title.contains("stewardship") -> ChallengeCategory.COMMUNITY

            else -> {
                // Heuristic fallback based on predominant physical resource
                if (log.waterSavedLiters > 0f) ChallengeCategory.WATER
                else if (log.wasteSavedKg > 0f) ChallengeCategory.ZERO_WASTE
                else if (log.co2SavedKg > 5.0f) ChallengeCategory.MOBILITY
                else ChallengeCategory.ENERGY
            }
        }
    }
}

