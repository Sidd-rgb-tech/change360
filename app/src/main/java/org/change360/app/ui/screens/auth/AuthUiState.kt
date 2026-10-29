package org.change360.app.ui.screens.auth

/**
 * Authentication mode supported by the CHANGE360 onboarding experience.
 */
enum class AuthMode(val label: String) {
    SIGN_UP(label = "Join Movement"),
    SIGN_IN(label = "Sign In"),
    GUEST(label = "Quick Pledge")
}

/**
 * Immutable UI State representing authentication screen state.
 *
 * Encapsulates practitioner input credentials, selected eco-pledge, loading flags,
 * error messages, and successful authentication status.
 *
 * @property mode The active authentication mode ([AuthMode.SIGN_UP], [AuthMode.SIGN_IN], or [AuthMode.GUEST]).
 * @property displayName The entered call sign or practitioner name.
 * @property email The entered practitioner email address.
 * @property selectedPledge The curated eco-pledge statement selected from the chip options.
 * @property customPledge An optional custom user-authored eco-pledge affirmation.
 * @property isLoading Indicates whether an authentication operation is actively running.
 * @property errorMessage Human-readable description of any validation or authentication failure.
 * @property isSuccess Flag set to true upon successful authentication or session creation.
 * @property pledgeOptions Curated starter eco-pledge affirmations.
 */
data class AuthUiState(
    val mode: AuthMode = AuthMode.SIGN_UP,
    val displayName: String = "",
    val email: String = "",
    val selectedPledge: String = DEFAULT_ECO_PLEDGE,
    val customPledge: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val pledgeOptions: List<String> = DEFAULT_PLEDGES
) {
    /**
     * Resolves the effective eco-pledge commitment prioritizing custom user text if provided.
     */
    val activePledge: String
        get() = customPledge.trim().ifBlank { selectedPledge.trim() }

    /**
     * Verifies basic client-side input validity according to the active [mode].
     */
    val isInputValid: Boolean
        get() = when (mode) {
            AuthMode.SIGN_IN -> email.trim().isNotBlank() && email.contains('@') && email.contains('.')
            AuthMode.SIGN_UP -> displayName.trim().length >= 2 &&
                    email.trim().isNotBlank() && email.contains('@') && email.contains('.') &&
                    activePledge.isNotBlank()
            AuthMode.GUEST -> displayName.trim().ifBlank { "Earth Guardian" }.length >= 2 &&
                    activePledge.isNotBlank()
        }

    companion object {
        const val DEFAULT_ECO_PLEDGE: String = "Cut single-use plastics & reduce daily carbon footprint"

        val DEFAULT_PLEDGES: List<String> = listOf(
            "Cut single-use plastics & reduce daily carbon footprint",
            "Adopt green daily commute & active micro-mobility",
            "Transition to plant-forward and seasonal eating",
            "Achieve zero-waste home composting & circular recycling",
            "Slash household vampire electricity & conserve energy"
        )
    }
}

