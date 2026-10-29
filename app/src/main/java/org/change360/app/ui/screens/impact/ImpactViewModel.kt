package org.change360.app.ui.screens.impact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.change360.app.domain.model.ImpactSummary
import org.change360.app.domain.model.TimeRange
import org.change360.app.domain.repository.ImpactRepository
import org.change360.app.domain.usecase.CalculateImpactStatsUseCase

/**
 * ViewModel governing planetary carbon mitigation and resource conservation accounting.
 *
 * Responsibilities:
 * - Manages time scope window selection ([TimeRange.WEEK], [TimeRange.MONTH], [TimeRange.ALL_TIME]).
 * - Recomputes category breakdowns, weekly momentum trends, and real-world conservation equivalents
 *   reactively via [CalculateImpactStatsUseCase].
 * - Exposes verified recent activity logs from [ImpactRepository].
 *
 * Compatible with [org.change360.app.di.ViewModelFactory] reflection-based constructor resolution.
 *
 * @param calculateImpactStatsUseCase Core use case calculating environmental metrics and equivalents.
 * @param impactRepository Repository providing reactive access to raw impact ledgers.
 */
class ImpactViewModel @JvmOverloads constructor(
    private val calculateImpactStatsUseCase: CalculateImpactStatsUseCase,
    private val impactRepository: ImpactRepository
) : ViewModel() {

    private val _timeRange = MutableStateFlow(TimeRange.WEEK)
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val analyticsFlow = _timeRange.flatMapLatest { range ->
        calculateImpactStatsUseCase.observeImpactAnalytics(range)
    }

    /**
     * Primary UI State stream reacting to time range transitions and raw ledger updates.
     */
    val uiState: StateFlow<ImpactUiState> = combine(
        _timeRange,
        analyticsFlow,
        impactRepository.observeAllLogs(),
        _isLoading,
        _errorMessage
    ) { range, analytics, allLogs, loading, error ->
        ImpactUiState(
            timeRange = range,
            analytics = analytics,
            summary = analytics.summary,
            recentLogs = allLogs.take(20),
            isLoading = loading,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ImpactUiState()
    )

    /**
     * Switches the active analytics time window filter.
     *
     * @param range The target [TimeRange] ([TimeRange.WEEK], [TimeRange.MONTH], or [TimeRange.ALL_TIME]).
     */
    fun onTimeRangeSelected(range: TimeRange) {
        _timeRange.value = range
        _errorMessage.value = null
    }

    /**
     * Deletes a specific impact log ledger entry by ID.
     */
    fun deleteLog(logId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = impactRepository.deleteLogById(logId)
            result.onFailure { error ->
                _errorMessage.value = error.message ?: "Failed to delete impact log."
            }
            _isLoading.value = false
        }
    }

    /**
     * Clears all recorded impact ledger entries.
     */
    fun clearAllLogs() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = impactRepository.clearAllLogs()
            result.onFailure { error ->
                _errorMessage.value = error.message ?: "Failed to clear impact history."
            }
            _isLoading.value = false
        }
    }

    /**
     * Dismisses any active error notification banner.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}

