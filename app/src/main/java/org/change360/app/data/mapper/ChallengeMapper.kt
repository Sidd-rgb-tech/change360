package org.change360.app.data.mapper

import org.change360.app.data.local.entity.ChallengeEntity
import org.change360.app.domain.model.Challenge
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ChallengeDifficulty

/**
 * Bi-directional mapper transforming between [ChallengeEntity] and [Challenge].
 */
fun ChallengeEntity.toDomain(): Challenge {
    return Challenge(
        id = id,
        title = title,
        description = description,
        category = ChallengeCategory.fromString(category),
        difficulty = ChallengeDifficulty.fromString(difficulty),
        xpReward = xpReward,
        co2SavedKg = co2SavedKg,
        waterSavedLiters = waterSavedLiters,
        wasteSavedKg = wasteSavedKg,
        totalSteps = totalSteps,
        completedSteps = completedSteps,
        isCompleted = isCompleted,
        completedAtEpochMillis = completedAtEpochMillis
    )
}

fun Challenge.toEntity(): ChallengeEntity {
    return ChallengeEntity(
        id = id,
        title = title,
        description = description,
        category = category.name,
        difficulty = difficulty.name,
        xpReward = xpReward,
        co2SavedKg = co2SavedKg,
        waterSavedLiters = waterSavedLiters,
        wasteSavedKg = wasteSavedKg,
        totalSteps = totalSteps,
        completedSteps = completedSteps,
        isCompleted = isCompleted,
        completedAtEpochMillis = completedAtEpochMillis
    )
}

