package org.change360.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an active sustainability practitioner profile.
 *
 * Persists personal credentials, verified eco-pledge declarations,
 * dynamic Aura progression levels, and ongoing daily climate action streaks.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "displayName")
    val displayName: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "ecoPledge")
    val ecoPledge: String,

    @ColumnInfo(name = "auraLevel")
    val auraLevel: Int = 1,

    @ColumnInfo(name = "xpPoints")
    val xpPoints: Int = 0,

    @ColumnInfo(name = "currentStreakDays")
    val currentStreakDays: Int = 0,

    @ColumnInfo(name = "createdAtEpochMillis")
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

