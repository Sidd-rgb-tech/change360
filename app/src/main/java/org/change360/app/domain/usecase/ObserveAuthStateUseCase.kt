package org.change360.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import org.change360.app.domain.model.User
import org.change360.app.domain.repository.AuthRepository

/**
 * Represents the authentication and profile hydration lifecycle of the active practitioner.
 */
sealed interface AuthState {

    /**
     * Initial transient state emitted while session tokens and local user records are read.
     */
    data object Loading : AuthState

    /**
     * Emitted when no active authenticated session exists or when the user signs out.
     */
    data object Unauthenticated : AuthState

    /**
     * Emitted when an active authenticated practitioner profile is verified.
     *
     * @property user The hydrated domain [User] entity.
     */
    data class Authenticated(val user: User) : AuthState

    /**
     * Helper indicating whether this state represents a fully authenticated practitioner.
     */
    val isAuthenticated: Boolean
        get() = this is Authenticated

    /**
     * Returns the [User] profile if authenticated, or null otherwise.
     */
    val userOrNull: User?
        get() = (this as? Authenticated)?.user

    /**
     * Helper indicating whether session verification is in progress.
     */
    val isLoading: Boolean
        get() = this is Loading
}

/**
 * Core Use Case governing reactive observation of user authentication status.
 *
 * Coordinates DataStore session indicators with Room practitioner profiles via [AuthRepository],
 * providing an offline-first single source of truth for navigation routing and profile display.
 */
class ObserveAuthStateUseCase(
    private val authRepository: AuthRepository
) {

    /**
     * Emits continuous updates to [AuthState].
     *
     * Begins with [AuthState.Loading] upon collection, followed by [AuthState.Authenticated]
     * if both DataStore authentication flag is true and the user record is populated in Room,
     * or [AuthState.Unauthenticated] otherwise.
     */
    operator fun invoke(): Flow<AuthState> {
        return combine(
            authRepository.observeIsAuthenticated(),
            authRepository.observeActiveUser()
        ) { isAuthenticated, user ->
            if (isAuthenticated && user != null) {
                AuthState.Authenticated(user)
            } else {
                AuthState.Unauthenticated
            }
        }
            .onStart { emit(AuthState.Loading) }
            .distinctUntilChanged()
    }

    /**
     * Synchronously retrieves a single-shot [AuthState] snapshot without active stream collection.
     */
    suspend fun getCurrentState(): AuthState {
        val user = authRepository.getActiveUser()
        return if (user != null) {
            AuthState.Authenticated(user)
        } else {
            AuthState.Unauthenticated
        }
    }
}

