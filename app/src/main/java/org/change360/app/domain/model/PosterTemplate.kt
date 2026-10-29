package org.change360.app.domain.model

/**
 * Visual styling theme preset for advocacy posters.
 *
 * Configures bio-luminescent gradient stops, contrast typography, and theme identity.
 */
enum class PosterAuraStyle(
    val styleKey: String,
    val displayName: String,
    val primaryColorHex: String,
    val secondaryColorHex: String,
    val backgroundHex: String
) {
    BIO_EMERALD(
        styleKey = "BIO_EMERALD",
        displayName = "Bio-Emerald Pulse",
        primaryColorHex = "#00E676",
        secondaryColorHex = "#00BFA5",
        backgroundHex = "#06140E"
    ),
    OCEANIC_PULSE(
        styleKey = "OCEANIC_PULSE",
        displayName = "Oceanic Vitality",
        primaryColorHex = "#00BFA5",
        secondaryColorHex = "#00E5FF",
        backgroundHex = "#06131A"
    ),
    SOLAR_FLARE(
        styleKey = "SOLAR_FLARE",
        displayName = "Solar Clean Energy",
        primaryColorHex = "#FFD740",
        secondaryColorHex = "#FF9100",
        backgroundHex = "#181204"
    ),
    MIDNIGHT_GAIA(
        styleKey = "MIDNIGHT_GAIA",
        displayName = "Midnight Gaia Stewardship",
        primaryColorHex = "#00E5FF",
        secondaryColorHex = "#7C4DFF",
        backgroundHex = "#0C081A"
    );

    companion object {
        fun fromKey(key: String?): PosterAuraStyle {
            if (key.isNullOrBlank()) return BIO_EMERALD
            return entries.find { it.styleKey.equals(key.trim(), ignoreCase = true) } ?: BIO_EMERALD
        }
    }
}

/**
 * Curated poster template design ready for personalization.
 */
data class PosterTemplate(
    val id: String,
    val title: String,
    val defaultQuote: String,
    val auraStyle: PosterAuraStyle,
    val accentColorHex: String
)

/**
 * User-customized advocacy poster entity saved locally.
 */
data class SavedPoster(
    val id: Long = 0L,
    val title: String,
    val pledgeQuote: String,
    val templateStyle: PosterAuraStyle,
    val accentColorHex: String,
    val exportedUriString: String? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

