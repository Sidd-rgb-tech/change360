package org.change360.app.ui.screens.impact

import org.change360.app.domain.model.ImpactLog
import org.change360.app.domain.model.ImpactSummary
import org.change360.app.domain.model.TimeRange
import org.change360.app.domain.usecase.DetailedImpactAnalytics

/**
 * Immutable UI State for the Carbon and Resource Impact Accounting screen.
 *
 * @property timeRange Active time scope window ([TimeRange.WEEK], [TimeRange.MONTH], or [TimeRange.ALL_TIME]).
 * @property analytics Detailed analytics bundle including carbon summaries, weekly momentum trends,
 *           top conservation category, and tangible planetary equivalents.
 * @property summary High-level summary metrics (CO2 avoided, water conserved, waste diverted).
 * @property recentLogs Recent immutable ledger entries recorded by the practitioner.
 * @property isLoading Indicates whether metric calculation or log deletion is running.
 * @property errorMessage Descriptive error details if metric computation fails.
 */
data class ImpactUiState(
    val timeRange: TimeRange = TimeRange.WEEK,
    val analytics: DetailedImpactAnalytics? = null,
    val summary: ImpactSummary = ImpactSummary(),
    val recentLogs: List<ImpactLog> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

