package org.change360.app.ui.screens.home

import org.change360.app.domain.model.ChallengeStepResult
import org.change360.app.domain.model.ImpactSummary
import org.change360.app.domain.usecase.DashboardOverview
import org.change360.app.domain.usecase.GetDashboardOverviewUseCase

/**
 * Immutable UI State representing the primary planetary Home dashboard.
 *
 * @property overview The aggregated domain dashboard state containing practitioner profile,
 *           active quest carousel, today's environmental metrics, and curated planetary tip.
 * @property isLoggingStep Indicates whether an incremental quest step progress update is running.
 * @property errorMessage Descriptive error details if quest advancement fails.
 * @property celebrationEvent Outcome emitted when a quest step triggers complete quest achievement.
 */
data class HomeUiState(
    val overview: DashboardOverview = DEFAULT_OVERVIEW,
    val isLoggingStep: Boolean = false,
    val errorMessage: String? = null,
    val celebrationEvent: ChallengeStepResult? = null
) {
    companion object {
        /**
         * Safe default snapshot utilized during initial stream collection.
         */
        val DEFAULT_OVERVIEW: DashboardOverview = DashboardOverview(
            user = null,
            activeChallenges = emptyList(),
            todayImpact = ImpactSummary(),
            completedChallengesCount = 0,
            totalChallengesCount = 0,
            dailyTip = GetDashboardOverviewUseCase.CURATED_TIPS.first()
        )
    }
}

