package org.change360.app.domain.model

/**
 * Standard classification taxonomy for sustainability challenges and environmental actions.
 *
 * Mapped directly to database entity representations and UI filtering chips.
 */
enum class ChallengeCategory(
    val displayName: String,
    val emoji: String,
    val description: String,
    val accentColorHex: String
) {
    ALL(
        displayName = "All Actions",
        emoji = "🌍",
        description = "Holistic planetary stewardship across all environmental fronts",
        accentColorHex = "#00E676"
    ),
    ENERGY(
        displayName = "Energy ⚡",
        emoji = "⚡",
        description = "Slashing grid electricity demand, vampire power, and thermal loss",
        accentColorHex = "#FFD740"
    ),
    WATER(
        displayName = "Water 💧",
        emoji = "💧",
        description = "Preserving freshwater reservoirs, micro-leak auditing, and greywater reuse",
        accentColorHex = "#00E5FF"
    ),
    ZERO_WASTE(
        displayName = "Zero-Waste ♻️",
        emoji = "♻️",
        description = "Single-use plastic elimination, closed-loop composting, and bulk refills",
        accentColorHex = "#00BFA5"
    ),
    MOBILITY(
        displayName = "Mobility 🚲",
        emoji = "🚲",
        description = "Active transit, micromobility, electric public transit, and car-free routines",
        accentColorHex = "#81C784"
    ),
    COMMUNITY(
        displayName = "Community 🌱",
        emoji = "🌱",
        description = "Rewilding, collective cleanups, policy advocacy, and public poster sharing",
        accentColorHex = "#69F0AE"
    );

    companion object {
        /**
         * Resolves a category instance safely from a persisted String token.
         */
        fun fromString(value: String?): ChallengeCategory {
            if (value.isNullOrBlank()) return ALL
            return entries.find { it.name.equals(value.trim(), ignoreCase = true) } ?: ALL
        }
    }
}

