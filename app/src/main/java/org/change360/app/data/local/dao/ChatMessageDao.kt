package org.change360.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.change360.app.data.local.entity.ChatMessageEntity

/**
 * Data Access Object for persisting dialogues with the AI Sustainability Coach.
 */
@Dao
interface ChatMessageDao {

    /**
     * Observes the complete dialogue history ordered chronologically.
     */
    @Query("SELECT * FROM chat_messages ORDER BY timestampEpochMillis ASC")
    fun observeConversation(): Flow<List<ChatMessageEntity>>

    /**
     * Observes the most recent dialogue messages limited by count in descending order.
     */
    @Query("SELECT * FROM chat_messages ORDER BY timestampEpochMillis DESC LIMIT :limit")
    fun observeRecentMessages(limit: Int): Flow<List<ChatMessageEntity>>

    /**
     * Inserts an individual chat message, returning its auto-generated ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    /**
     * Inserts a batch of chat messages.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    /**
     * Clears all recorded dialogue messages.
     */
    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()

    /**
     * Retrieves the total count of stored chat messages.
     */
    @Query("SELECT COUNT(*) FROM chat_messages")
    suspend fun getMessageCount(): Int

    /**
     * Deletes a specific message by its row identifier.
     */
    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)
}

