package org.change360.app.domain.model

/**
 * Difficulty stratification for sustainability quests.
 */
enum class ChallengeDifficulty(val label: String, val colorHex: String) {
    EASY(label = "Easy", colorHex = "#69F0AE"),
    MEDIUM(label = "Medium", colorHex = "#FFD740"),
    HARD(label = "Hard", colorHex = "#FF5252");

    companion object {
        fun fromString(value: String?): ChallengeDifficulty {
            if (value.isNullOrBlank()) return MEDIUM
            return entries.find { it.name.equals(value.trim(), ignoreCase = true) } ?: MEDIUM
        }
    }
}

/**
 * Domain representation of an environmental challenge quest.
 *
 * Tracks multi-step progression toward planetary conservation goals,
 * quantifying carbon mitigation, water preservation, and landfill diversion.
 */
data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val category: ChallengeCategory,
    val difficulty: ChallengeDifficulty,
    val xpReward: Int,
    val co2SavedKg: Float,
    val waterSavedLiters: Float,
    val wasteSavedKg: Float,
    val totalSteps: Int,
    val completedSteps: Int = 0,
    val isCompleted: Boolean = false,
    val completedAtEpochMillis: Long? = null
) {
    /**
     * Normalized completion progress (0.0f - 1.0f).
     */
    val progress: Float
        get() = if (totalSteps > 0) {
            (completedSteps.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)
        } else 0f

    /**
     * Number of remaining incremental steps required to claim quest victory.
     */
    val remainingSteps: Int
        get() = (totalSteps - completedSteps).coerceAtLeast(0)

    /**
     * Verifies if this challenge is active and still has pending actions.
     */
    val isPending: Boolean
        get() = !isCompleted && completedSteps < totalSteps
}

/**
 * Outcome model emitted when a user logs an incremental progress step.
 */
data class ChallengeStepResult(
    val updatedChallenge: Challenge,
    val isJustCompleted: Boolean,
    val xpAwarded: Int,
    val co2SavedKg: Float,
    val waterSavedLiters: Float,
    val wasteSavedKg: Float
)

