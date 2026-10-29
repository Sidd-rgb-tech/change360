package org.change360.app.ui.screens.poster

import org.change360.app.domain.model.PosterAuraStyle
import org.change360.app.domain.model.PosterTemplate
import org.change360.app.domain.model.SavedPoster
import org.change360.app.domain.usecase.PosterVerifiedBadge

/**
 * Typealias preserving the alternative naming convention specified in PLAN.md.
 */
typealias PosterGeneratorUiState = PosterUiState

/**
 * Immutable UI State for the Advocacy Poster Studio screen.
 *
 * @property selectedStyle Active color and aura background gradient theme preset.
 * @property headline Main user-customized advocacy affirmation headline.
 * @property pledgeQuote Supporting personal eco-pledge or planetary declaration.
 * @property accentColorHex Primary visual accent color stop for borders and badges.
 * @property includeStatsBadge Toggle controlling display of the verified live practitioner impact badge.
 * @property templates Curated starter poster templates ready for personalization.
 * @property savedPosters List of locally saved practitioner posters.
 * @property verifiedBadge Live practitioner impact badge data (name, streak, aura level, CO2 avoided).
 * @property isSaving Indicates whether poster metadata is currently persisting to storage.
 * @property isExporting Indicates whether bitmap export / share packaging is running.
 * @property lastExportedUri File URI string of the most recently rendered bitmap for sharing.
 * @property lastSavedPosterId Database primary key ID of the latest saved poster.
 * @property errorMessage Descriptive error details if saving or exporting fails.
 */
data class PosterUiState(
    val selectedStyle: PosterAuraStyle = PosterAuraStyle.BIO_EMERALD,
    val headline: String = DEFAULT_HEADLINE,
    val pledgeQuote: String = DEFAULT_QUOTE,
    val accentColorHex: String = PosterAuraStyle.BIO_EMERALD.primaryColorHex,
    val includeStatsBadge: Boolean = true,
    val templates: List<PosterTemplate> = emptyList(),
    val savedPosters: List<SavedPoster> = emptyList(),
    val verifiedBadge: PosterVerifiedBadge? = null,
    val isSaving: Boolean = false,
    val isExporting: Boolean = false,
    val lastExportedUri: String? = null,
    val lastSavedPosterId: Long? = null,
    val errorMessage: String? = null
) {
    companion object {
        const val DEFAULT_HEADLINE: String = "Act Today For Tomorrow"
        const val DEFAULT_QUOTE: String = "Every small habit shift regenerates our collective biosphere."
    }
}

