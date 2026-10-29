package org.change360.app.domain.model

/**
 * Filter window selector for aggregated sustainability impact reports.
 */
enum class TimeRange(val label: String, val durationMillis: Long) {
    WEEK(label = "This Week", durationMillis = 7L * 24 * 60 * 60 * 1000L),
    MONTH(label = "This Month", durationMillis = 30L * 24 * 60 * 60 * 1000L),
    ALL_TIME(label = "All Time", durationMillis = 0L);

    /**
     * Calculates the starting epoch millisecond boundary for this time range.
     */
    fun getSinceTimestamp(now: Long = System.currentTimeMillis()): Long {
        return if (durationMillis == 0L) 0L else (now - durationMillis).coerceAtLeast(0L)
    }
}

/**
 * Quantified breakdown of environmental mitigation within a specific category.
 */
data class CategoryImpactBreakdown(
    val category: ChallengeCategory,
    val co2Kg: Float,
    val waterLiters: Float,
    val wasteKg: Float,
    val actionsCount: Int,
    val percentageOfTotalCo2: Float
)

/**
 * Aggregated domain analytics model combining total resources saved,
 * tangible real-world equivalents, and category distribution.
 */
data class ImpactSummary(
    val totalCo2SavedKg: Float = 0f,
    val totalWaterSavedLiters: Float = 0f,
    val totalWasteSavedKg: Float = 0f,
    val treesPlantedEquivalent: Float = 0f,
    val carKmAvoided: Float = 0f,
    val plasticBottlesSaved: Int = 0,
    val actionsLoggedCount: Int = 0,
    val timeRange: TimeRange = TimeRange.ALL_TIME,
    val categoryBreakdowns: List<CategoryImpactBreakdown> = emptyList()
) {
    companion object {
        /**
         * Environmental conversion coefficients from PLAN.md:
         * - 1 mature tree absorbs ~21.77 kg CO2 per year
         * - 1 kg CO2 avoided is equivalent to ~4.1 passenger car kilometers avoided
         * - 1 kg plastic waste diverted is equivalent to ~40 single-use half-liter plastic bottles
         */
        const val CO2_KG_PER_TREE_YEAR: Float = 21.77f
        const val KM_PER_KG_CO2: Float = 4.1f
        const val BOTTLES_PER_KG_WASTE: Int = 40

        /**
         * Pure functional calculation factory generating [ImpactSummary] from raw logs.
         */
        fun calculate(
            logs: List<ImpactLog>,
            timeRange: TimeRange = TimeRange.ALL_TIME
        ): ImpactSummary {
            val totalCo2 = logs.sumOf { it.co2SavedKg.toDouble() }.toFloat()
            val totalWater = logs.sumOf { it.waterSavedLiters.toDouble() }.toFloat()
            val totalWaste = logs.sumOf { it.wasteSavedKg.toDouble() }.toFloat()

            val trees = if (totalCo2 > 0f) totalCo2 / CO2_KG_PER_TREE_YEAR else 0f
            val carKm = if (totalCo2 > 0f) totalCo2 * KM_PER_KG_CO2 else 0f
            val bottles = if (totalWaste > 0f) (totalWaste * BOTTLES_PER_KG_WASTE).toInt() else 0

            return ImpactSummary(
                totalCo2SavedKg = totalCo2,
                totalWaterSavedLiters = totalWater,
                totalWasteSavedKg = totalWaste,
                treesPlantedEquivalent = trees,
                carKmAvoided = carKm,
                plasticBottlesSaved = bottles,
                actionsLoggedCount = logs.size,
                timeRange = timeRange,
                categoryBreakdowns = emptyList()
            )
        }
    }
}

