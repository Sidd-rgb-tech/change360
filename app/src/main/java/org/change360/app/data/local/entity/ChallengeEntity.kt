package org.change360.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing an environmental sustainability action quest.
 *
 * Tracks multi-step execution progress, quantified environmental metrics
 * (CO2 mitigation, water conservation, and waste diversion), and completion state.
 */
@Entity(
    tableName = "challenges",
    indices = [
        Index(value = ["category"]),
        Index(value = ["isCompleted"])
    ]
)
data class ChallengeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "difficulty")
    val difficulty: String,

    @ColumnInfo(name = "xpReward")
    val xpReward: Int,

    @ColumnInfo(name = "co2SavedKg")
    val co2SavedKg: Float,

    @ColumnInfo(name = "waterSavedLiters")
    val waterSavedLiters: Float,

    @ColumnInfo(name = "wasteSavedKg")
    val wasteSavedKg: Float,

    @ColumnInfo(name = "totalSteps")
    val totalSteps: Int,

    @ColumnInfo(name = "completedSteps")
    val completedSteps: Int = 0,

    @ColumnInfo(name = "isCompleted")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "completedAtEpochMillis")
    val completedAtEpochMillis: Long? = null
)

