package org.change360.app.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.change360.app.data.local.dao.PosterDao
import org.change360.app.data.local.entity.PosterEntity
import org.change360.app.data.mapper.toDomain
import org.change360.app.domain.model.PosterAuraStyle
import org.change360.app.domain.model.PosterTemplate
import org.change360.app.domain.model.SavedPoster
import org.change360.app.domain.repository.PosterRepository

/**
 * Concrete implementation of [PosterRepository] managing advocacy poster templates,
 * user customizations, and saved rendered poster metadata via Room [PosterDao].
 *
 * Responsibilities:
 * - Exposes streams of user-customized advocacy posters.
 * - Provides curated aesthetic templates featuring bio-luminescent aura gradients.
 * - Persists and updates exported poster image file URIs for sharing.
 */
class PosterRepositoryImpl(
    private val posterDao: PosterDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PosterRepository {

    override fun observeSavedPosters(): Flow<List<SavedPoster>> {
        return posterDao.observeSavedPosters()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observePosterById(id: Long): Flow<SavedPoster?> {
        return posterDao.observePosterById(id)
            .map { it?.toDomain() }
            .flowOn(ioDispatcher)
    }

    override suspend fun getPosterById(id: Long): SavedPoster? = withContext(ioDispatcher) {
        posterDao.getPosterById(id)?.toDomain()
    }

    override fun getCuratedTemplates(): List<PosterTemplate> {
        return CURATED_TEMPLATES
    }

    override suspend fun savePoster(
        title: String,
        pledgeQuote: String,
        templateStyle: String,
        accentColorHex: String,
        exportedUriString: String?
    ): Result<Long> = withContext(ioDispatcher) {
        runCatching {
            val cleanTitle = title.trim()
            val cleanQuote = pledgeQuote.trim()

            require(cleanTitle.isNotBlank()) { "Poster title cannot be blank." }
            require(cleanQuote.isNotBlank()) { "Poster pledge quote cannot be blank." }

            val entity = PosterEntity(
                title = cleanTitle,
                pledgeQuote = cleanQuote,
                templateStyle = templateStyle,
                accentColorHex = accentColorHex,
                exportedUriString = exportedUriString,
                createdAtEpochMillis = System.currentTimeMillis()
            )

            posterDao.insertPoster(entity)
        }
    }

    override suspend fun updateExportedUri(
        id: Long,
        exportedUriString: String
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            require(exportedUriString.isNotBlank()) { "Exported URI string cannot be empty." }
            posterDao.updateExportedUri(id, exportedUriString)
        }
    }

    override suspend fun deletePosterById(id: Long): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            posterDao.deletePosterById(id)
        }
    }

    override suspend fun clearAllPosters(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            posterDao.clearAllPosters()
        }
    }

    companion object {
        /**
         * Curated visual templates designed according to CHANGE360 design tokens.
         */
        val CURATED_TEMPLATES: List<PosterTemplate> = listOf(
            PosterTemplate(
                id = "template_bio_emerald",
                title = "Bio-Luminescent Pulse",
                defaultQuote = "Every daily action ripples across our biosphere. Choose regeneration.",
                auraStyle = PosterAuraStyle.BIO_EMERALD,
                accentColorHex = "#00E676"
            ),
            PosterTemplate(
                id = "template_oceanic_pulse",
                title = "Oceanic Vitality",
                defaultQuote = "Protecting living waters begins at home. Small swaps create tidal waves.",
                auraStyle = PosterAuraStyle.OCEANIC_PULSE,
                accentColorHex = "#00BFA5"
            ),
            PosterTemplate(
                id = "template_solar_flare",
                title = "Solar Clean Energy",
                defaultQuote = "Harnessing the power of renewable choices for an electrified, circular tomorrow.",
                auraStyle = PosterAuraStyle.SOLAR_FLARE,
                accentColorHex = "#FFD740"
            ),
            PosterTemplate(
                id = "template_midnight_gaia",
                title = "Midnight Gaia Stewardship",
                defaultQuote = "In harmony with Earth: 360 degrees of mindful, climate-positive living.",
                auraStyle = PosterAuraStyle.MIDNIGHT_GAIA,
                accentColorHex = "#00E5FF"
            )
        )
    }
}

