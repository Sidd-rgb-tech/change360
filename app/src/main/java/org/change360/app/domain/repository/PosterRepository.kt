package org.change360.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.change360.app.domain.model.PosterTemplate
import org.change360.app.domain.model.SavedPoster

/**
 * Domain contract for managing advocacy poster templates, user customizations,
 * and rendered poster image metadata.
 */
interface PosterRepository {

    /**
     * Observes all saved user advocacy posters ordered by creation timestamp descending.
     */
    fun observeSavedPosters(): Flow<List<SavedPoster>>

    /**
     * Observes a specific saved poster by its identifier.
     */
    fun observePosterById(id: Long): Flow<SavedPoster?>

    /**
     * Synchronously retrieves a saved poster snapshot.
     */
    suspend fun getPosterById(id: Long): SavedPoster?

    /**
     * Returns curated starter poster templates.
     */
    fun getCuratedTemplates(): List<PosterTemplate>

    /**
     * Saves a customized advocacy poster configuration.
     */
    suspend fun savePoster(
        title: String,
        pledgeQuote: String,
        templateStyle: String,
        accentColorHex: String,
        exportedUriString: String? = null
    ): Result<Long>

    /**
     * Updates the exported image file URI string for a specific poster.
     */
    suspend fun updateExportedUri(id: Long, exportedUriString: String): Result<Unit>

    /**
     * Deletes a poster by primary key.
     */
    suspend fun deletePosterById(id: Long): Result<Unit>

    /**
     * Clears all saved posters.
     */
    suspend fun clearAllPosters(): Result<Unit>
}

