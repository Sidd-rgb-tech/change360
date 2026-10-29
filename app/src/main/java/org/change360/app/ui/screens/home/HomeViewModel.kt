package org.change360.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.change360.app.domain.model.ChallengeStepResult
import org.change360.app.domain.usecase.CompleteChallengeUseCase
import org.change360.app.domain.usecase.DashboardOverview
import org.change360.app.domain.usecase.GetDashboardOverviewUseCase

/**
 * ViewModel governing the primary Home dashboard.
 *
 * Responsibilities:
 * - Exposes real-time [DashboardOverview] stream via [overview] [StateFlow].
 * - Exposes unified [HomeUiState] coordinating dashboard metrics with quest action feedback.
 * - Handles quest step progression and victory celebrations via [CompleteChallengeUseCase].
 *
 * Compatible with [org.change360.app.di.ViewModelFactory] reflection-based constructor resolution.
 *
 * @param getDashboardOverviewUseCase Core use case aggregating practitioner profile, quests, and impact.
 * @param completeChallengeUseCase Core use case advancing quest steps and recording impact logs.
 */
class HomeViewModel @JvmOverloads constructor(
    private val getDashboardOverviewUseCase: GetDashboardOverviewUseCase,
    private val completeChallengeUseCase: CompleteChallengeUseCase
) : ViewModel() {

    /**
     * Primary domain dashboard state stream directly exposing [DashboardOverview].
     */
    val overview: StateFlow<DashboardOverview> = getDashboardOverviewUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HomeUiState.DEFAULT_OVERVIEW
        )

    private val _transientState = MutableStateFlow(TransientHomeState())

    /**
     * Consolidated Compose UI State combining live dashboard metrics and action states.
     */
    val uiState: StateFlow<HomeUiState> = combine(
        overview,
        _transientState
    ) { dashboardData, transient ->
        HomeUiState(
            overview = dashboardData,
            isLoggingStep = transient.isLoggingStep,
            errorMessage = transient.errorMessage,
            celebrationEvent = transient.celebrationEvent
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState()
    )

    /**
     * Records an incremental step progress on the target sustainability quest.
     *
     * On final step completion, triggers celebratory feedback and registers XP/impact gains.
     *
     * @param challengeId Unique identifier of the quest being advanced.
     */
    fun logQuestStep(challengeId: String) {
        val cleanId = challengeId.trim()
        if (cleanId.isBlank()) return

        viewModelScope.launch {
            _transientState.update { it.copy(isLoggingStep = true, errorMessage = null) }

            val result = completeChallengeUseCase(cleanId)

            result.onSuccess { stepResult ->
                _transientState.update {
                    it.copy(
                        isLoggingStep = false,
                        celebrationEvent = if (stepResult.isJustCompleted) stepResult else null,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _transientState.update {
                    it.copy(
                        isLoggingStep = false,
                        errorMessage = error.message ?: "Failed to record quest step."
                    )
                }
            }
        }
    }

    /**
     * Completes all remaining steps on a multi-step quest immediately.
     */
    fun completeQuestDirectly(challengeId: String) {
        val cleanId = challengeId.trim()
        if (cleanId.isBlank()) return

        viewModelScope.launch {
            _transientState.update { it.copy(isLoggingStep = true, errorMessage = null) }

            val result = completeChallengeUseCase.completeDirectly(cleanId)

            result.onSuccess { stepResult ->
                _transientState.update {
                    it.copy(
                        isLoggingStep = false,
                        celebrationEvent = if (stepResult.isJustCompleted) stepResult else null,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _transientState.update {
                    it.copy(
                        isLoggingStep = false,
                        errorMessage = error.message ?: "Failed to complete quest."
                    )
                }
            }
        }
    }

    /**
     * Clears any active celebratory snackbar or milestone dialog state.
     */
    fun clearCelebration() {
        _transientState.update { it.copy(celebrationEvent = null) }
    }

    /**
     * Dismisses any active error notification.
     */
    fun clearError() {
        _transientState.update { it.copy(errorMessage = null) }
    }

    /**
     * Private internal data holder for transient UI states.
     */
    private data class TransientHomeState(
        val isLoggingStep: Boolean = false,
        val errorMessage: String? = null,
        val celebrationEvent: ChallengeStepResult? = null
    )
}

