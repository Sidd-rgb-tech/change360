package org.change360.app.ui.screens.ai

import org.change360.app.domain.model.ChatMessage

/**
 * Immutable UI State for the Eco-AI Coach conversational experience.
 *
 * @property messages Chronological stream of user prompts and AI sustainability guidance.
 * @property inputPrompt Current text typed into the conversational input field.
 * @property isTyping Indicates whether the AI Coach is actively generating a response.
 * @property quickChips Curated contextual prompt shortcuts displayed above the input bar.
 * @property errorMessage Details of any dispatch or network/generation failure.
 */
data class AiAssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputPrompt: String = "",
    val isTyping: Boolean = false,
    val quickChips: List<String> = emptyList(),
    val errorMessage: String? = null
) {
    /**
     * Determines whether the user prompt submission button should be enabled.
     */
    val isSendEnabled: Boolean
        get() = inputPrompt.trim().isNotBlank() && !isTyping
}

