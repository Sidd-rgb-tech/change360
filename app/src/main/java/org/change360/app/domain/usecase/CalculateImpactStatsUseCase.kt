package org.change360.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.change360.app.domain.model.CategoryImpactBreakdown
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ImpactLog
import org.change360.app.domain.model.ImpactSummary
import org.change360.app.domain.model.TimeRange
import org.change360.app.domain.repository.ImpactRepository

/**
 * Tangible real-world conservation equivalents translating raw metric numbers
 * into intuitive ecological milestones.
 */
data class ImpactEquivalents(
    val treesAbsorbingCo2: Float,
    val passengerCarKmAvoided: Float,
    val plasticBottlesDiverted: Int,
    val ledBulbHoursEquivalent: Float,
    val smartphoneChargesEquivalent: Int
)

/**
 * Detailed analytics bundle combining aggregated impact, weekly momentum trends,
 * top performing conservation category, and tangible planetary equivalents.
 */
data class DetailedImpactAnalytics(
    val summary: ImpactSummary,
    val weeklyTrendPercentage: Float,
    val topCategory: ChallengeCategory?,
    val equivalents: ImpactEquivalents
)

/**
 * Core Use Case governing the mathematical aggregation of environmental impact metrics.
 *
 * Responsibilities:
 * - Aggregates raw [ImpactLog] ledgers into structured [ImpactSummary] reports.
 * - Categorizes environmental contributions across Energy, Water, Zero-Waste, Mobility, and Community.
 * - Computes percentage changes (weekly trend trajectory) comparing current vs prior periods.
 * - Derives tangible real-world equivalents (mature trees absorbing carbon, car km avoided, plastic bottles diverted).
 */
class CalculateImpactStatsUseCase(
    private val impactRepository: ImpactRepository
) {

    /**
     * Observes the aggregated [ImpactSummary] for the selected [TimeRange] window.
     */
    operator fun invoke(timeRange: TimeRange = TimeRange.ALL_TIME): Flow<ImpactSummary> {
        return impactRepository.observeImpactSummary(timeRange)
    }

    /**
     * Observes enriched [DetailedImpactAnalytics] containing summary data, weekly momentum trends,
     * and tangible planetary equivalents.
     */
    fun observeImpactAnalytics(timeRange: TimeRange = TimeRange.ALL_TIME): Flow<DetailedImpactAnalytics> {
        return combine(
            impactRepository.observeImpactSummary(timeRange),
            impactRepository.observeAllLogs()
        ) { summary, allLogs ->
            val weeklyTrend = calculateWeeklyTrendPercentage(allLogs)
            val topCategory = summary.categoryBreakdowns
                .maxByOrNull { it.co2Kg }
                ?.takeIf { it.co2Kg > 0f }
                ?.category

            val equivalents = calculateEquivalents(
                co2Kg = summary.totalCo2SavedKg,
                wasteKg = summary.totalWasteSavedKg
            )

            DetailedImpactAnalytics(
                summary = summary,
                weeklyTrendPercentage = weeklyTrend,
                topCategory = topCategory,
                equivalents = equivalents
            )
        }
    }

    /**
     * Pure transformation function calculating [ImpactSummary] from an arbitrary collection of logs.
     *
     * @param logs The raw ledger entries to aggregate.
     * @param timeRange The scope window being analyzed.
     */
    fun calculate(
        logs: List<ImpactLog>,
        timeRange: TimeRange = TimeRange.ALL_TIME
    ): ImpactSummary {
        val totalCo2 = logs.sumOf { it.co2SavedKg.toDouble() }.toFloat()
        val totalWater = logs.sumOf { it.waterSavedLiters.toDouble() }.toFloat()
        val totalWaste = logs.sumOf { it.wasteSavedKg.toDouble() }.toFloat()

        val trees = if (totalCo2 > 0f) totalCo2 / ImpactSummary.CO2_KG_PER_TREE_YEAR else 0f
        val carKm = if (totalCo2 > 0f) totalCo2 * ImpactSummary.KM_PER_KG_CO2 else 0f
        val bottles = if (totalWaste > 0f) (totalWaste * ImpactSummary.BOTTLES_PER_KG_WASTE).toInt() else 0

        val breakdowns = calculateCategoryBreakdowns(logs, totalCo2)

        return ImpactSummary(
            totalCo2SavedKg = totalCo2,
            totalWaterSavedLiters = totalWater,
            totalWasteSavedKg = totalWaste,
            treesPlantedEquivalent = trees,
            carKmAvoided = carKm,
            plasticBottlesSaved = bottles,
            actionsLoggedCount = logs.size,
            timeRange = timeRange,
            categoryBreakdowns = breakdowns
        )
    }

    /**
     * Computes categorical breakdowns across each non-ALL [ChallengeCategory].
     */
    fun calculateCategoryBreakdowns(
        logs: List<ImpactLog>,
        totalCo2: Float = logs.sumOf { it.co2SavedKg.toDouble() }.toFloat()
    ): List<CategoryImpactBreakdown> {
        val groupedLogs = logs.groupBy { deduceCategory(it) }

        return ChallengeCategory.entries
            .filter { it != ChallengeCategory.ALL }
            .map { cat ->
                val group = groupedLogs[cat] ?: emptyList()
                val catCo2 = group.sumOf { it.co2SavedKg.toDouble() }.toFloat()
                val catWater = group.sumOf { it.waterSavedLiters.toDouble() }.toFloat()
                val catWaste = group.sumOf { it.wasteSavedKg.toDouble() }.toFloat()
                val percentage = if (totalCo2 > 0f) (catCo2 / totalCo2) * 100f else 0f

                CategoryImpactBreakdown(
                    category = cat,
                    co2Kg = catCo2,
                    waterLiters = catWater,
                    wasteKg = catWaste,
                    actionsCount = group.size,
                    percentageOfTotalCo2 = percentage
                )
            }
    }

    /**
     * Calculates the weekly momentum percentage comparing the most recent 7-day window
     * against the preceding 7-day baseline.
     *
     * @return Positive percentage indicating growth in conservation, or negative indicating slowdown.
     */
    fun calculateWeeklyTrendPercentage(
        logs: List<ImpactLog>,
        now: Long = System.currentTimeMillis()
    ): Float {
        val sevenDaysMillis = 7L * 24 * 60 * 60 * 1000L
        val currentWeekStart = now - sevenDaysMillis
        val priorWeekStart = now - (2 * sevenDaysMillis)

        val currentWeekCo2 = logs
            .filter { it.loggedAtEpochMillis in currentWeekStart..now }
            .sumOf { it.co2SavedKg.toDouble() }
            .toFloat()

        val priorWeekCo2 = logs
            .filter { it.loggedAtEpochMillis in priorWeekStart until currentWeekStart }
            .sumOf { it.co2SavedKg.toDouble() }
            .toFloat()

        return when {
            priorWeekCo2 > 0f -> ((currentWeekCo2 - priorWeekCo2) / priorWeekCo2) * 100f
            currentWeekCo2 > 0f -> 100f
            else -> 0f
        }
    }

    /**
     * Computes tangible real-world conservation equivalents.
     */
    fun calculateEquivalents(co2Kg: Float, wasteKg: Float): ImpactEquivalents {
        val trees = if (co2Kg > 0f) co2Kg / ImpactSummary.CO2_KG_PER_TREE_YEAR else 0f
        val carKm = if (co2Kg > 0f) co2Kg * ImpactSummary.KM_PER_KG_CO2 else 0f
        val bottles = if (wasteKg > 0f) (wasteKg * ImpactSummary.BOTTLES_PER_KG_WASTE).toInt() else 0
        val bulbHours = if (co2Kg > 0f) co2Kg * LED_BULB_HOURS_PER_KG_CO2 else 0f
        val phoneCharges = if (co2Kg > 0f) (co2Kg * PHONE_CHARGES_PER_KG_CO2).toInt() else 0

        return ImpactEquivalents(
            treesAbsorbingCo2 = trees,
            passengerCarKmAvoided = carKm,
            plasticBottlesDiverted = bottles,
            ledBulbHoursEquivalent = bulbHours,
            smartphoneChargesEquivalent = phoneCharges
        )
    }

    /**
     * Classifies an [ImpactLog] entry into a specific [ChallengeCategory] based on
     * source challenge identifier, action title keywords, or predominant physical resource.
     */
    fun deduceCategory(log: ImpactLog): ChallengeCategory {
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
                if (log.waterSavedLiters > 0f) ChallengeCategory.WATER
                else if (log.wasteSavedKg > 0f) ChallengeCategory.ZERO_WASTE
                else if (log.co2SavedKg > 5.0f) ChallengeCategory.MOBILITY
                else ChallengeCategory.ENERGY
            }
        }
    }

    companion object {
        const val LED_BULB_HOURS_PER_KG_CO2: Float = 50f
        const val PHONE_CHARGES_PER_KG_CO2: Float = 120f
    }
}

