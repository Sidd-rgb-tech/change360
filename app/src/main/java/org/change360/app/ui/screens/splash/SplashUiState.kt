package org.change360.app.ui.screens.splash

import org.change360.app.ui.navigation.AppDestination

/**
 * Immutable UI State for the 3-second bio-luminescent splash screen.
 *
 * @property isAuraReady Indicates whether the 3,000ms breathing aura pulse sequence has elapsed.
 * @property nextDestination Target navigation route once authentication and aura timer complete.
 */
data class SplashUiState(
    val isAuraReady: Boolean = false,
    val nextDestination: AppDestination? = null
)

