package org.change360.app.domain.model

/**
 * Domain model representing an authenticated sustainability practitioner profile.
 *
 * Encapsulates user identity, personal eco-pledge commitment, Aura energy level,
 * accumulated experience points (XP), and active consecutive daily action streaks.
 */
data class User(
    val id: String,
    val displayName: String,
    val email: String,
    val ecoPledge: String,
    val auraLevel: Int = 1,
    val xpPoints: Int = 0,
    val currentStreakDays: Int = 0,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
) {
    /**
     * Threshold of XP required to reach the subsequent Aura level tier.
     */
    val xpForCurrentLevel: Int = (auraLevel - 1) * XP_PER_LEVEL
    val xpForNextLevel: Int = auraLevel * XP_PER_LEVEL

    /**
     * Normalized completion progress (0.0f - 1.0f) within the current Aura tier.
     */
    val progressInCurrentLevel: Float
        get() {
            if (XP_PER_LEVEL <= 0) return 0f
            val xpInTier = (xpPoints - xpForCurrentLevel).coerceAtLeast(0)
            return (xpInTier.toFloat() / XP_PER_LEVEL.toFloat()).coerceIn(0f, 1f)
        }

    /**
     * Remaining XP needed to trigger the next Aura level breakthrough.
     */
    val xpRemainingForNextLevel: Int
        get() = (xpForNextLevel - xpPoints).coerceAtLeast(0)

    /**
     * Human-readable thematic title reflecting planetary stewardship mastery.
     */
    val auraTitle: String
        get() = when (auraLevel) {
            1 -> "Eco Seedling"
            2 -> "Green Sprout"
            3 -> "Planetary Guardian"
            4 -> "Regenerative Champion"
            5 -> "Solar Sovereign"
            else -> "Earth Luminary Tier $auraLevel"
        }

    companion object {
        const val XP_PER_LEVEL: Int = 500
    }
}

