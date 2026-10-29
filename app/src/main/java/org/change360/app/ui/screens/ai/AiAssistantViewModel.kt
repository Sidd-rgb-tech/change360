package org.change360.app.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.change360.app.domain.model.ChatMessage
import org.change360.app.domain.usecase.SendAiMessageUseCase

/**
 * ViewModel governing the conversational Eco-AI Coach interface.
 *
 * Responsibilities:
 * - Observes and streams chat history from Room storage via [SendAiMessageUseCase].
 * - Dispatches user prompts and curated contextual quick chips.
 * - Handles typing indicator states and conversation history clearing.
 *
 * Compatible with [org.change360.app.di.ViewModelFactory] reflection-based constructor resolution.
 *
 * @param sendAiMessageUseCase Core use case coordinating sustainability intelligence and message persistence.
 */
class AiAssistantViewModel(
    private val sendAiMessageUseCase: SendAiMessageUseCase
) : ViewModel() {

    /**
     * Direct reactive stream of the conversational history.
     */
    val messages: StateFlow<List<ChatMessage>> = sendAiMessageUseCase.observeConversation()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(
        AiAssistantUiState(quickChips = sendAiMessageUseCase.getQuickPromptChips())
    )

    /**
     * Consolidated UI state stream for Compose screens.
     */
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            messages.collect { msgList ->
                _uiState.update { it.copy(messages = msgList) }
            }
        }
    }

    /**
     * Updates the text entered into the natural language input bar.
     */
    fun onInputPromptChanged(prompt: String) {
        _uiState.update {
            it.copy(
                inputPrompt = prompt,
                errorMessage = null
            )
        }
    }

    /**
     * Dispatches a user prompt to the Eco-AI Coach.
     *
     * @param prompt Text prompt to send (defaults to the current typed input).
     */
    fun sendMessage(prompt: String = _uiState.value.inputPrompt) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isBlank() || _uiState.value.isTyping) return

        // Clear input bar immediately for responsive UI feel
        _uiState.update {
            it.copy(
                inputPrompt = "",
                errorMessage = null
            )
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isTyping = true) }

            val result = sendAiMessageUseCase(cleanPrompt)

            result.onFailure { error ->
                val msg = error.message ?: "Failed to receive response from Eco-AI Coach."
                _uiState.update { it.copy(errorMessage = msg) }
            }

            _uiState.update { it.copy(isTyping = false) }
        }
    }

    /**
     * Dispatches a curated quick-start prompt chip.
     *
     * @param chipText The text of the selected suggestion chip.
     * @param autoSend Whether to immediately send the message or populate the input field.
     */
    fun onQuickPromptSelected(chipText: String, autoSend: Boolean = true) {
        val cleanChip = chipText.trim()
        if (cleanChip.isBlank()) return

        if (autoSend) {
            sendMessage(cleanChip)
        } else {
            _uiState.update {
                it.copy(
                    inputPrompt = cleanChip,
                    errorMessage = null
                )
            }
        }
    }

    /**
     * Clears all conversational messages from local storage.
     */
    fun clearHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTyping = true) }
            val result = sendAiMessageUseCase.clearHistory()
            result.onFailure { error ->
                val msg = error.message ?: "Failed to clear conversation history."
                _uiState.update { it.copy(errorMessage = msg) }
            }
            _uiState.update { it.copy(isTyping = false) }
        }
    }

    /**
     * Deletes a specific message from conversational memory.
     */
    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            val result = sendAiMessageUseCase.deleteMessage(id)
            result.onFailure { error ->
                val msg = error.message ?: "Failed to delete message."
                _uiState.update { it.copy(errorMessage = msg) }
            }
        }
    }

    /**
     * Dismisses any active conversational error message banner.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
