package org.change360.app.data.mapper

import org.change360.app.data.local.entity.PosterEntity
import org.change360.app.domain.model.PosterAuraStyle
import org.change360.app.domain.model.SavedPoster

/**
 * Bi-directional mapper transforming between [PosterEntity] and [SavedPoster].
 */
fun PosterEntity.toDomain(): SavedPoster {
    return SavedPoster(
        id = id,
        title = title,
        pledgeQuote = pledgeQuote,
        templateStyle = PosterAuraStyle.fromKey(templateStyle),
        accentColorHex = accentColorHex,
        exportedUriString = exportedUriString,
        createdAtEpochMillis = createdAtEpochMillis
    )
}

fun SavedPoster.toEntity(): PosterEntity {
    return PosterEntity(
        id = id,
        title = title,
        pledgeQuote = pledgeQuote,
        templateStyle = templateStyle.styleKey,
        accentColorHex = accentColorHex,
        exportedUriString = exportedUriString,
        createdAtEpochMillis = createdAtEpochMillis
    )
}

