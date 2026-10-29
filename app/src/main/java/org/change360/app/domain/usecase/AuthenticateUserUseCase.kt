package org.change360.app.domain.usecase

import org.change360.app.domain.model.User
import org.change360.app.domain.repository.AuthRepository

/**
 * Encapsulates intention parameters for authenticating a practitioner profile.
 */
sealed interface AuthRequest {

    /**
     * Standard sign-in utilizing a registered email address.
     */
    data class SignIn(val email: String) : AuthRequest

    /**
     * Account registration with full practitioner identity and sustainability commitment.
     */
    data class SignUp(
        val displayName: String,
        val email: String,
        val ecoPledge: String
    ) : AuthRequest

    /**
     * Zero-friction guest onboarding with default or personalized eco-pledge affirmations.
     */
    data class Guest(
        val displayName: String = "Earth Guardian",
        val ecoPledge: String = "Cut single-use plastics & reduce daily carbon footprint"
    ) : AuthRequest
}

/**
 * Core Use Case governing authentication, registration, guest onboarding, and session termination.
 *
 * Validates domain constraints (email format, minimum name length, pledge presence)
 * prior to delegating to [AuthRepository].
 */
class AuthenticateUserUseCase(
    private val authRepository: AuthRepository
) {

    /**
     * Authenticates a user according to the specified [AuthRequest].
     */
    suspend operator fun invoke(request: AuthRequest): Result<User> {
        return when (request) {
            is AuthRequest.SignIn -> signIn(request.email)
            is AuthRequest.SignUp -> signUp(request.displayName, request.email, request.ecoPledge)
            is AuthRequest.Guest -> signInAsGuest(request.displayName, request.ecoPledge)
        }
    }

    /**
     * Signs in an existing practitioner by email address.
     *
     * @param email The practitioner's registered email address.
     * @return [Result] wrapping the authenticated [User] profile, or an error.
     */
    suspend fun signIn(email: String): Result<User> {
        val sanitizedEmail = email.trim()
        if (sanitizedEmail.isBlank()) {
            return Result.failure(IllegalArgumentException("Email address cannot be blank."))
        }
        if (!isValidEmail(sanitizedEmail)) {
            return Result.failure(IllegalArgumentException("Please provide a valid email address (e.g. name@domain.eco)."))
        }

        return authRepository.signIn(sanitizedEmail)
    }

    /**
     * Registers a new sustainability practitioner.
     *
     * @param displayName The practitioner's public profile name (minimum 2 characters).
     * @param email The practitioner's email address.
     * @param ecoPledge The practitioner's foundational environmental commitment.
     * @return [Result] wrapping the newly created [User] profile.
     */
    suspend fun signUp(
        displayName: String,
        email: String,
        ecoPledge: String
    ): Result<User> {
        val cleanName = displayName.trim()
        val cleanEmail = email.trim()
        val cleanPledge = ecoPledge.trim()

        if (cleanName.length < 2) {
            return Result.failure(IllegalArgumentException("Display name must be at least 2 characters long."))
        }
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(IllegalArgumentException("Please provide a valid email address."))
        }
        if (cleanPledge.isBlank()) {
            return Result.failure(IllegalArgumentException("Please select or specify a personal eco-pledge commitment."))
        }

        return authRepository.signUp(cleanName, cleanEmail, cleanPledge)
    }

    /**
     * Fast-tracks guest onboarding for immediate action logging without password friction.
     */
    suspend fun signInAsGuest(
        displayName: String = "Earth Guardian",
        ecoPledge: String = "Cut single-use plastics & reduce daily carbon footprint"
    ): Result<User> {
        val cleanName = displayName.trim().ifBlank { "Earth Guardian" }
        val cleanPledge = ecoPledge.trim().ifBlank { "Cut single-use plastics & reduce daily carbon footprint" }
        return authRepository.signInAsGuest(cleanName, cleanPledge)
    }

    /**
     * Updates an active practitioner's eco-pledge declaration.
     */
    suspend fun updateEcoPledge(userId: String, ecoPledge: String): Result<Unit> {
        val cleanPledge = ecoPledge.trim()
        if (cleanPledge.isBlank()) {
            return Result.failure(IllegalArgumentException("Eco-pledge declaration cannot be blank."))
        }
        return authRepository.updateEcoPledge(userId, cleanPledge)
    }

    /**
     * Terminates the active session and clears persisted authentication state.
     */
    suspend fun signOut(): Result<Unit> {
        return authRepository.signOut()
    }

    /**
     * Pure validation verifying standard email structure.
     */
    private fun isValidEmail(email: String): Boolean {
        if (email.length < 5) return false
        val atIndex = email.indexOf('@')
        val dotIndex = email.lastIndexOf('.')
        return atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < email.length - 1
    }
}

