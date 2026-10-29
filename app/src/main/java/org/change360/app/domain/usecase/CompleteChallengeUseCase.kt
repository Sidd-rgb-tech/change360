package org.change360.app.domain.usecase

import org.change360.app.domain.model.Challenge
import org.change360.app.domain.model.ChallengeStepResult
import org.change360.app.domain.repository.ChallengeRepository

/**
 * Core Use Case governing sustainability challenge progression, milestone validation,
 * and completion celebration handling.
 *
 * Coordinates with [ChallengeRepository] to:
 * 1. Validate that the target quest exists and has pending actions.
 * 2. Advance the challenge's completed steps counter atomically.
 * 3. When the final step is reached, trigger XP rewards, Aura level recalculation,
 *    streak progression, and an immutable entry in the environmental impact ledger.
 */
class CompleteChallengeUseCase(
    private val challengeRepository: ChallengeRepository
) {

    /**
     * Records an incremental progress step on the target sustainability challenge.
     *
     * @param challengeId Unique identifier of the challenge quest.
     * @return [Result] wrapping [ChallengeStepResult] detailing updated progress, completion flag,
     *         and awarded rewards.
     */
    suspend operator fun invoke(challengeId: String): Result<ChallengeStepResult> {
        val cleanId = challengeId.trim()
        if (cleanId.isBlank()) {
            return Result.failure(IllegalArgumentException("Challenge ID cannot be blank."))
        }

        val existing = challengeRepository.getChallengeById(cleanId)
            ?: return Result.failure(NoSuchElementException("Challenge with ID '$cleanId' not found."))

        if (existing.isCompleted) {
            return Result.success(
                ChallengeStepResult(
                    updatedChallenge = existing,
                    isJustCompleted = false,
                    xpAwarded = 0,
                    co2SavedKg = 0f,
                    waterSavedLiters = 0f,
                    wasteSavedKg = 0f
                )
            )
        }

        return challengeRepository.logStep(cleanId)
    }

    /**
     * Advances all remaining steps of a challenge to claim immediate completion.
     *
     * Useful for one-tap complete actions on multi-step quests.
     *
     * @param challengeId Unique identifier of the challenge.
     * @return [Result] wrapping the final [ChallengeStepResult].
     */
    suspend fun completeDirectly(challengeId: String): Result<ChallengeStepResult> {
        val cleanId = challengeId.trim()
        if (cleanId.isBlank()) {
            return Result.failure(IllegalArgumentException("Challenge ID cannot be blank."))
        }

        val challenge = challengeRepository.getChallengeById(cleanId)
            ?: return Result.failure(NoSuchElementException("Challenge with ID '$cleanId' not found."))

        if (challenge.isCompleted) {
            return Result.success(
                ChallengeStepResult(
                    updatedChallenge = challenge,
                    isJustCompleted = false,
                    xpAwarded = 0,
                    co2SavedKg = 0f,
                    waterSavedLiters = 0f,
                    wasteSavedKg = 0f
                )
            )
        }

        var latestResult: ChallengeStepResult? = null
        val remaining = challenge.remainingSteps

        for (step in 0 until remaining) {
            val stepResult = challengeRepository.logStep(cleanId).getOrElse { err ->
                return Result.failure(err)
            }
            latestResult = stepResult
            if (stepResult.isJustCompleted) {
                break
            }
        }

        return Result.success(
            latestResult ?: ChallengeStepResult(
                updatedChallenge = challenge,
                isJustCompleted = false,
                xpAwarded = 0,
                co2SavedKg = 0f,
                waterSavedLiters = 0f,
                wasteSavedKg = 0f
            )
        )
    }

    /**
     * Resets a challenge back to 0 steps and marks it pending for re-playability.
     */
    suspend fun resetChallenge(challengeId: String): Result<Unit> {
        val cleanId = challengeId.trim()
        if (cleanId.isBlank()) {
            return Result.failure(IllegalArgumentException("Challenge ID cannot be blank."))
        }
        return challengeRepository.resetChallenge(cleanId)
    }

    /**
     * Pure validation verifying whether a challenge is eligible to receive a step log.
     */
    fun canLogStep(challenge: Challenge): Boolean {
        return !challenge.isCompleted && challenge.completedSteps < challenge.totalSteps
    }
}

