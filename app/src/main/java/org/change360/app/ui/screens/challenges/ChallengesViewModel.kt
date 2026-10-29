package org.change360.app.ui.screens.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ChallengeStepResult
import org.change360.app.domain.repository.ChallengeRepository
import org.change360.app.domain.usecase.CompleteChallengeUseCase

/**
 * ViewModel orchestrating sustainability challenge inventory, category filtering,
 * and step progression mechanics.
 *
 * Exposes a unified [ChallengesUiState] to [ChallengesScreen] and notifies practitioners
 * of completion events via [ChallengeStepResult].
 *
 * Compatible with [org.change360.app.di.ViewModelFactory] reflection-based constructor resolution.
 *
 * @param completeChallengeUseCase Core use case advancing quest steps and logging impact.
 * @param challengeRepository Repository observing challenges across all categories.
 */
class ChallengesViewModel(
    private val completeChallengeUseCase: CompleteChallengeUseCase,
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(ChallengeCategory.ALL)
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _celebrationEvent = MutableStateFlow<ChallengeStepResult?>(null)

    private val _uiState = MutableStateFlow(ChallengesUiState())

    /**
     * Primary UI State stream reacting to challenge inventory and category filter changes.
     */
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                _selectedCategory,
                challengeRepository.observeAllChallenges(),
                challengeRepository.observeCompletedCount(),
                challengeRepository.observeTotalCount()
            ) { category, allChallenges, completedCount, totalCount ->
                val filteredChallenges = if (category == ChallengeCategory.ALL) {
                    allChallenges
                } else {
                    allChallenges.filter { it.category == category }
                }
                _uiState.update { current ->
                    current.copy(
                        selectedCategory = category,
                        challenges = filteredChallenges,
                        completedCount = completedCount,
                        totalCount = totalCount
                    )
                }
            }.collect()
        }
    }

    /**
     * Updates the active category filter for the quest list.
     *
     * @param category The selected [ChallengeCategory] tab.
     */
    fun onCategorySelected(category: ChallengeCategory) {
        _selectedCategory.value = category
        _errorMessage.value = null
        _uiState.update { current ->
            current.copy(
                selectedCategory = category,
                errorMessage = null
            )
        }
    }

    /**
     * Logs an incremental progress step on the designated challenge.
     *
     * @param challengeId Unique identifier of the challenge.
     */
    fun logChallengeStep(challengeId: String) {
        val cleanId = challengeId.trim()
        if (cleanId.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = completeChallengeUseCase(cleanId)

            result.onSuccess { stepResult ->
                if (stepResult.isJustCompleted) {
                    _celebrationEvent.value = stepResult
                    _uiState.update { it.copy(celebrationEvent = stepResult) }
                }
            }.onFailure { error ->
                val msg = error.message ?: "Failed to log challenge step."
                _errorMessage.value = msg
                _uiState.update { it.copy(errorMessage = msg) }
            }

            _isLoading.value = false
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Advances all remaining steps on a multi-step quest immediately to victory.
     *
     * @param challengeId Unique identifier of the challenge.
     */
    fun completeChallengeDirectly(challengeId: String) {
        val cleanId = challengeId.trim()
        if (cleanId.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = completeChallengeUseCase.completeDirectly(cleanId)

            result.onSuccess { stepResult ->
                if (stepResult.isJustCompleted) {
                    _celebrationEvent.value = stepResult
                    _uiState.update { it.copy(celebrationEvent = stepResult) }
                }
            }.onFailure { error ->
                val msg = error.message ?: "Failed to complete challenge."
                _errorMessage.value = msg
                _uiState.update { it.copy(errorMessage = msg) }
            }

            _isLoading.value = false
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Resets a challenge back to 0 steps for re-playability.
     */
    fun resetChallenge(challengeId: String) {
        val cleanId = challengeId.trim()
        if (cleanId.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _uiState.update { it.copy(isLoading = true) }
            val result = completeChallengeUseCase.resetChallenge(cleanId)
            result.onFailure { error ->
                val msg = error.message ?: "Failed to reset challenge."
                _errorMessage.value = msg
                _uiState.update { it.copy(errorMessage = msg) }
            }
            _isLoading.value = false
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Clears the current completion celebration event after display.
     */
    fun clearCelebration() {
        _celebrationEvent.value = null
        _uiState.update { it.copy(celebrationEvent = null) }
    }

    /**
     * Dismisses any active error notification banner.
     */
    fun clearError() {
        _errorMessage.value = null
        _uiState.update { it.copy(errorMessage = null) }
    }
}
