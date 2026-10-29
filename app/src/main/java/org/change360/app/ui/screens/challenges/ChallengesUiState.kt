package org.change360.app.ui.screens.challenges

import org.change360.app.domain.model.Challenge
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ChallengeStepResult

/**
 * Immutable UI State for the Eco-Action Quests screen.
 *
 * @property selectedCategory The active classification filter ([ChallengeCategory.ALL], [ChallengeCategory.ENERGY], etc.).
 * @property challenges The list of quests filtered by the active [selectedCategory].
 * @property completedCount Cumulative count of completed quests.
 * @property totalCount Overall count of quests registered in the system.
 * @property isLoading Indicates whether a background quest action is in flight.
 * @property errorMessage Descriptive error details if step progression fails.
 * @property celebrationEvent Milestone celebration triggered when a quest reaches full completion.
 */
data class ChallengesUiState(
    val selectedCategory: ChallengeCategory = ChallengeCategory.ALL,
    val challenges: List<Challenge> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val celebrationEvent: ChallengeStepResult? = null
)

