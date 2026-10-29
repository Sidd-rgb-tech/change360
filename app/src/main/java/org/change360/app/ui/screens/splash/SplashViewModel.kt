package org.change360.app.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.change360.app.domain.usecase.AuthState
import org.change360.app.domain.usecase.ObserveAuthStateUseCase
import org.change360.app.ui.navigation.AppDestination

/**
 * ViewModel managing the initial 3-second Bio-Luminescent Aura sequence and session verification.
 *
 * Runs a minimum 3,000ms animation timer concurrently with [ObserveAuthStateUseCase]
 * to cleanly transition into [AppDestination.Main] (if authenticated) or [AppDestination.Auth].
 *
 * Compatible with [org.change360.app.di.ViewModelFactory] reflection-based constructor resolution.
 *
 * @param observeAuthStateUseCase Core use case verifying persisted practitioner credentials.
 */
class SplashViewModel(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        resolveStartupRouting()
    }

    /**
     * Executes the synchronized 3-second aura breathing animation and auth state resolution.
     */
    fun resolveStartupRouting() {
        viewModelScope.launch {
            val auraTimer = async { delay(3000L) }
            val authStateResult = async {
                observeAuthStateUseCase()
                    .first { it !is AuthState.Loading }
            }

            val authState = authStateResult.await()
            auraTimer.await()

            val target = if (authState.isAuthenticated) {
                AppDestination.Main
            } else {
                AppDestination.Auth
            }

            _uiState.update {
                it.copy(
                    isAuraReady = true,
                    nextDestination = target
                )
            }
        }
    }
}

