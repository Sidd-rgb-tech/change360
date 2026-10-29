package org.change360.app.data.mapper

import org.change360.app.data.local.entity.ImpactLogEntity
import org.change360.app.domain.model.ImpactLog

/**
 * Bi-directional mapper transforming between [ImpactLogEntity] and [ImpactLog].
 */
fun ImpactLogEntity.toDomain(): ImpactLog {
    return ImpactLog(
        id = id,
        userId = userId,
        sourceChallengeId = sourceChallengeId,
        actionTitle = actionTitle,
        co2SavedKg = co2SavedKg,
        waterSavedLiters = waterSavedLiters,
        wasteSavedKg = wasteSavedKg,
        loggedAtEpochMillis = loggedAtEpochMillis
    )
}

fun ImpactLog.toEntity(): ImpactLogEntity {
    return ImpactLogEntity(
        id = id,
        userId = userId,
        sourceChallengeId = sourceChallengeId,
        actionTitle = actionTitle,
        co2SavedKg = co2SavedKg,
        waterSavedLiters = waterSavedLiters,
        wasteSavedKg = wasteSavedKg,
        loggedAtEpochMillis = loggedAtEpochMillis
    )
}

