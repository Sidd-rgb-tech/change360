package org.change360.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.change360.app.domain.model.ChatMessage
import org.change360.app.domain.repository.AiAssistantRepository

/**
 * Core Use Case managing conversational dialogue with the Eco-AI Coach.
 *
 * Coordinates:
 * - Sanitization and validation of incoming user prompts.
 * - Persistence of user inquiries and generated coaching responses into local storage.
 * - Real-time conversational stream observation and recommendation chips retrieval.
 */
class SendAiMessageUseCase(
    private val aiAssistantRepository: AiAssistantRepository
) {

    /**
     * Submits a user prompt to the Eco-AI Coach.
     *
     * Validates that the prompt is non-empty, triggers the contextual sustainability
     * intelligence engine, and persists both user prompt and AI response in Room.
     *
     * @param userPrompt The natural language inquiry or topic from the practitioner.
     * @return [Result] wrapping the newly generated AI [ChatMessage] response.
     */
    suspend operator fun invoke(userPrompt: String): Result<ChatMessage> {
        val cleanPrompt = userPrompt.trim()
        if (cleanPrompt.isBlank()) {
            return Result.failure(IllegalArgumentException("Message prompt cannot be blank."))
        }

        return aiAssistantRepository.sendMessage(cleanPrompt)
    }

    /**
     * Observes the complete conversational stream in chronological order.
     */
    fun observeConversation(): Flow<List<ChatMessage>> {
        return aiAssistantRepository.observeConversation()
    }

    /**
     * Observes recent messages limited to the specified window count.
     */
    fun observeRecentMessages(limit: Int = 50): Flow<List<ChatMessage>> {
        val safeLimit = limit.coerceAtLeast(1)
        return aiAssistantRepository.observeRecentMessages(safeLimit)
    }

    /**
     * Retrieves curated quick-start prompt chips for the conversational UI.
     */
    fun getQuickPromptChips(): List<String> {
        return aiAssistantRepository.getQuickPromptChips()
    }

    /**
     * Clears all conversational history from local storage.
     */
    suspend fun clearHistory(): Result<Unit> {
        return aiAssistantRepository.clearHistory()
    }

    /**
     * Removes an individual message from the conversation ledger.
     */
    suspend fun deleteMessage(id: Long): Result<Unit> {
        return aiAssistantRepository.deleteMessageById(id)
    }
}

