package org.change360.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing an exchange in the sustainability AI Coach dialogue.
 *
 * Persists user inquiries and generative guidance, linking recommendations
 * directly to actionable sustainability categories.
 */
@Entity(
    tableName = "chat_messages",
    indices = [
        Index(value = ["timestampEpochMillis"])
    ]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "sender")
    val sender: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "suggestedActionCategory")
    val suggestedActionCategory: String? = null,

    @ColumnInfo(name = "timestampEpochMillis")
    val timestampEpochMillis: Long = System.currentTimeMillis()
)

