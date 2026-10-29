package org.change360.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing an immutable ledger record of an environmental action.
 *
 * Captures atomic contributions toward planetary conservation: CO2 avoided,
 * water preserved, and landfill waste diverted.
 */
@Entity(
    tableName = "impact_logs",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["loggedAtEpochMillis"])
    ]
)
data class ImpactLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "userId")
    val userId: String,

    @ColumnInfo(name = "sourceChallengeId")
    val sourceChallengeId: String? = null,

    @ColumnInfo(name = "actionTitle")
    val actionTitle: String,

    @ColumnInfo(name = "co2SavedKg")
    val co2SavedKg: Float,

    @ColumnInfo(name = "waterSavedLiters")
    val waterSavedLiters: Float,

    @ColumnInfo(name = "wasteSavedKg")
    val wasteSavedKg: Float,

    @ColumnInfo(name = "loggedAtEpochMillis")
    val loggedAtEpochMillis: Long = System.currentTimeMillis()
)

