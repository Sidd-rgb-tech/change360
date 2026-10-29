package org.change360.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.change360.app.domain.model.ChatMessage

/**
 * Domain contract for conversational interaction with the Eco-AI Coach.
 *
 * Persists prompt exchanges, provides contextual environmental coaching,
 * and maintains curated prompt recommendation chips.
 */
interface AiAssistantRepository {

    /**
     * Observes the complete dialogue history ordered chronologically.
     */
    fun observeConversation(): Flow<List<ChatMessage>>

    /**
     * Observes the most recent dialogue messages limited by count.
     */
    fun observeRecentMessages(limit: Int): Flow<List<ChatMessage>>

    /**
     * Submits a user prompt, analyzes current sustainability context,
     * generates intelligent guidance, and persists both exchanges.
     */
    suspend fun sendMessage(userPrompt: String): Result<ChatMessage>

    /**
     * Clears all conversational history from local storage.
     */
    suspend fun clearHistory(): Result<Unit>

    /**
     * Deletes an individual message by identifier.
     */
    suspend fun deleteMessageById(id: Long): Result<Unit>

    /**
     * Returns curated quick-start prompt chips for the conversational UI.
     */
    fun getQuickPromptChips(): List<String>
}

