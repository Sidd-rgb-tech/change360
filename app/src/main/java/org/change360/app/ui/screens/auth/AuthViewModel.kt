package org.change360.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.change360.app.domain.usecase.AuthRequest
import org.change360.app.domain.usecase.AuthenticateUserUseCase

/**
 * ViewModel managing authentication, registration, and guest onboarding workflows.
 *
 * Exposes an immutable [AuthUiState] stream via [StateFlow] and coordinates with
 * [AuthenticateUserUseCase] to validate inputs and persist active practitioner sessions.
 *
 * Compatible with [org.change360.app.di.ViewModelFactory] reflection-based constructor resolution.
 *
 * @param authenticateUserUseCase Core use case governing practitioner authentication and session management.
 */
class AuthViewModel(
    private val authenticateUserUseCase: AuthenticateUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Toggles between [AuthMode.SIGN_UP], [AuthMode.SIGN_IN], and [AuthMode.GUEST].
     */
    fun onModeChanged(mode: AuthMode) {
        _uiState.update { current ->
            current.copy(
                mode = mode,
                errorMessage = null
            )
        }
    }

    /**
     * Updates the practitioner's display name or call sign.
     */
    fun onDisplayNameChanged(displayName: String) {
        _uiState.update { current ->
            current.copy(
                displayName = displayName,
                errorMessage = null
            )
        }
    }

    /**
     * Updates the practitioner's email address.
     */
    fun onEmailChanged(email: String) {
        _uiState.update { current ->
            current.copy(
                email = email,
                errorMessage = null
            )
        }
    }

    /**
     * Selects a curated starter eco-pledge option.
     */
    fun onPledgeSelected(pledge: String) {
        _uiState.update { current ->
            current.copy(
                selectedPledge = pledge,
                customPledge = "",
                errorMessage = null
            )
        }
    }

    /**
     * Updates a user-defined custom eco-pledge statement.
     */
    fun onCustomPledgeChanged(customPledge: String) {
        _uiState.update { current ->
            current.copy(
                customPledge = customPledge,
                errorMessage = null
            )
        }
    }

    /**
     * Dispatches authentication based on the currently selected [AuthMode].
     */
    fun submit() {
        when (_uiState.value.mode) {
            AuthMode.SIGN_UP -> signUp()
            AuthMode.SIGN_IN -> signIn()
            AuthMode.GUEST -> signInAsGuest()
        }
    }

    /**
     * Signs in an existing practitioner by email address.
     *
     * @param email The registered email address (defaults to current state).
     */
    fun signIn(email: String = _uiState.value.email) {
        executeAuth(AuthRequest.SignIn(email = email))
    }

    /**
     * Registers a new practitioner profile with display name, email, and foundational pledge.
     *
     * @param displayName The public call sign (defaults to current state).
     * @param email The contact email (defaults to current state).
     * @param ecoPledge The practitioner pledge (defaults to current active pledge).
     */
    fun signUp(
        displayName: String = _uiState.value.displayName,
        email: String = _uiState.value.email,
        ecoPledge: String = _uiState.value.activePledge
    ) {
        executeAuth(
            AuthRequest.SignUp(
                displayName = displayName,
                email = email,
                ecoPledge = ecoPledge
            )
        )
    }

    /**
     * Fast-tracks onboarding with zero password friction as a guest guardian.
     *
     * @param displayName Optional custom display name (defaults to "Earth Guardian").
     * @param ecoPledge Optional custom pledge (defaults to active pledge).
     */
    fun signInAsGuest(
        displayName: String = _uiState.value.displayName.ifBlank { "Earth Guardian" },
        ecoPledge: String = _uiState.value.activePledge
    ) {
        executeAuth(
            AuthRequest.Guest(
                displayName = displayName,
                ecoPledge = ecoPledge
            )
        )
    }

    /**
     * Terminates the active practitioner session and resets credentials.
     */
    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authenticateUserUseCase.signOut()
                .onSuccess {
                    _uiState.update {
                        AuthUiState(
                            mode = AuthMode.SIGN_IN,
                            isSuccess = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to sign out."
                        )
                    }
                }
        }
    }

    /**
     * Clears any active error message banner.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Resets the success state flag following navigation consumption.
     */
    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    /**
     * Internal execution pipeline dispatching [AuthRequest] through [AuthenticateUserUseCase].
     */
    private fun executeAuth(request: AuthRequest) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authenticateUserUseCase(request)

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = error.message ?: "Authentication failed. Please verify your entries."
                    )
                }
            }
        }
    }
}

