package org.change360.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.change360.app.domain.model.PosterAuraStyle
import org.change360.app.domain.model.PosterTemplate
import org.change360.app.domain.model.SavedPoster
import org.change360.app.domain.model.User
import org.change360.app.domain.repository.PosterRepository

/**
 * Encapsulates the configuration parameters required to persist a customized advocacy poster.
 */
data class SavePosterRequest(
    val title: String,
    val pledgeQuote: String,
    val templateStyle: PosterAuraStyle,
    val accentColorHex: String = templateStyle.primaryColorHex,
    val exportedUriString: String? = null
)

/**
 * Verified practitioner impact badge embedded into exported advocacy posters.
 */
data class PosterVerifiedBadge(
    val practitionerName: String,
    val co2SavedKg: Float,
    val streakDays: Int,
    val auraLevel: Int,
    val auraTitle: String
)

/**
 * Core Use Case governing the persistence, retrieval, and metadata updates of advocacy posters.
 *
 * Coordinates:
 * - Validating customized poster content (titles, pledge declarations, style tokens).
 * - Persisting poster configurations into local storage via [PosterRepository].
 * - Updating exported bitmap file URIs following render capture for sharing sheets.
 * - Supplying curated starter templates and constructing verified impact badge overlays.
 */
class SaveGeneratedPosterUseCase(
    private val posterRepository: PosterRepository
) {

    /**
     * Persists a customized advocacy poster configuration.
     *
     * @param request The [SavePosterRequest] containing headlines, pledge quotes, and style choices.
     * @return [Result] wrapping the newly generated database primary key ID.
     */
    suspend operator fun invoke(request: SavePosterRequest): Result<Long> {
        val cleanTitle = request.title.trim()
        val cleanQuote = request.pledgeQuote.trim()

        if (cleanTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Poster title headline cannot be blank."))
        }
        if (cleanQuote.isBlank()) {
            return Result.failure(IllegalArgumentException("Poster eco-pledge quote cannot be blank."))
        }

        val resolvedColor = request.accentColorHex.trim().ifBlank {
            request.templateStyle.primaryColorHex
        }

        return posterRepository.savePoster(
            title = cleanTitle,
            pledgeQuote = cleanQuote,
            templateStyle = request.templateStyle.styleKey,
            accentColorHex = resolvedColor,
            exportedUriString = request.exportedUriString
        )
    }

    /**
     * Convenience overload accepting individual poster parameters.
     */
    suspend operator fun invoke(
        title: String,
        pledgeQuote: String,
        templateStyle: PosterAuraStyle,
        accentColorHex: String = templateStyle.primaryColorHex,
        exportedUriString: String? = null
    ): Result<Long> {
        return invoke(
            SavePosterRequest(
                title = title,
                pledgeQuote = pledgeQuote,
                templateStyle = templateStyle,
                accentColorHex = accentColorHex,
                exportedUriString = exportedUriString
            )
        )
    }

    /**
     * Attaches the file URI of an exported bitmap render to an existing saved poster.
     */
    suspend fun updateExportedUri(posterId: Long, exportedUriString: String): Result<Unit> {
        val cleanUri = exportedUriString.trim()
        if (cleanUri.isBlank()) {
            return Result.failure(IllegalArgumentException("Exported URI string cannot be blank."))
        }
        return posterRepository.updateExportedUri(posterId, cleanUri)
    }

    /**
     * Observes all saved user advocacy posters ordered by creation timestamp descending.
     */
    fun observeSavedPosters(): Flow<List<SavedPoster>> {
        return posterRepository.observeSavedPosters()
    }

    /**
     * Observes an individual saved poster by its primary key ID.
     */
    fun observePosterById(id: Long): Flow<SavedPoster?> {
        return posterRepository.observePosterById(id)
    }

    /**
     * Retrieves a single saved poster snapshot.
     */
    suspend fun getPosterById(id: Long): SavedPoster? {
        return posterRepository.getPosterById(id)
    }

    /**
     * Retrieves curated starter poster templates ready for user customization.
     */
    fun getCuratedTemplates(): List<PosterTemplate> {
        return posterRepository.getCuratedTemplates()
    }

    /**
     * Deletes a saved poster by primary key.
     */
    suspend fun deletePoster(id: Long): Result<Unit> {
        return posterRepository.deletePosterById(id)
    }

    /**
     * Clears all saved user posters from storage.
     */
    suspend fun clearAllPosters(): Result<Unit> {
        return posterRepository.clearAllPosters()
    }

    /**
     * Constructs a verified sustainability badge data model combining active user credentials
     * and cumulative environmental metrics for inclusion on exported posters.
     */
    fun buildVerifiedBadge(
        user: User?,
        totalCo2SavedKg: Float
    ): PosterVerifiedBadge {
        val name = user?.displayName?.ifBlank { "Climate Champion" } ?: "Climate Champion"
        val streak = user?.currentStreakDays?.coerceAtLeast(1) ?: 1
        val level = user?.auraLevel?.coerceAtLeast(1) ?: 1
        val title = user?.auraTitle ?: "Eco Seedling"

        return PosterVerifiedBadge(
            practitionerName = name,
            co2SavedKg = totalCo2SavedKg.coerceAtLeast(0f),
            streakDays = streak,
            auraLevel = level,
            auraTitle = title
        )
    }
}

