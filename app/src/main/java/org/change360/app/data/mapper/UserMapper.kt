package org.change360.app.data.mapper

import org.change360.app.data.local.entity.UserEntity
import org.change360.app.domain.model.User

/**
 * Bi-directional mapper transforming between [UserEntity] and [User].
 */
fun UserEntity.toDomain(): User {
    return User(
        id = id,
        displayName = displayName,
        email = email,
        ecoPledge = ecoPledge,
        auraLevel = auraLevel,
        xpPoints = xpPoints,
        currentStreakDays = currentStreakDays,
        createdAtEpochMillis = createdAtEpochMillis
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        displayName = displayName,
        email = email,
        ecoPledge = ecoPledge,
        auraLevel = auraLevel,
        xpPoints = xpPoints,
        currentStreakDays = currentStreakDays,
        createdAtEpochMillis = createdAtEpochMillis
    )
}

