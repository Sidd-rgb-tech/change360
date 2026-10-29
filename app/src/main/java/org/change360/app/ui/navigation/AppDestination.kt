package org.change360.app.ui.navigation

import kotlinx.serialization.Serializable
import org.change360.app.ui.components.Change360Tab

/**
 * Type-safe navigation routes for CHANGE360 using Kotlinx Serialization.
 *
 * Defines the root navigation destinations ([Splash], [Auth], [Main]) as well as the
 * five persistent planetary action tabs ([Home], [AiAssistant], [Challenges], [Impact], [PosterGenerator]).
 */
@Serializable
sealed interface AppDestination {

    // --- Root Level Destinations ---

    /**
     * Splash screen route displaying the 3-second bio-luminescent aura animation while
     * validating practitioner session state.
     */
    @Serializable
    data object Splash : AppDestination

    /**
     * Authentication screen route supporting Sign In, Sign Up, and Quick Eco-Pledge (Guest mode).
     */
    @Serializable
    data object Auth : AppDestination

    /**
     * Root container route hosting [MainScaffold], persistent [Change360BottomBar],
     * and the nested 5-tab navigation graph.
     */
    @Serializable
    data object Main : AppDestination

    // --- 5 Main Planetary Tabs (Nested in MainScaffold) ---

    /**
     * Tab 1: Planetary Home Dashboard — displays aura energy level, streak, snapshot metrics,
     * active challenges, and quick actions.
     */
    @Serializable
    data object Home : AppDestination

    /**
     * Tab 2: Eco-AI Coach — conversational AI sustainability guidance, prompt chips,
     * and tailored recommendations.
     */
    @Serializable
    data object AiAssistant : AppDestination

    /**
     * Tab 3: Eco-Action Quests — categorized environmental challenges, progress step logging,
     * and XP rewards.
     */
    @Serializable
    data object Challenges : AppDestination

    /**
     * Tab 4: Carbon & Resource Impact Tracker — real-world equivalencies (trees, car km,
     * plastic bottles), charts, and timeline logs.
     */
    @Serializable
    data object Impact : AppDestination

    /**
     * Tab 5: Advocacy Poster Studio — customizable live aura poster generator with verified
     * impact stats and social sharing.
     */
    @Serializable
    data object PosterGenerator : AppDestination

    companion object {
        /**
         * List of the 5 top-level tab destinations in bottom navigation bar order.
         */
        val mainTabs: List<AppDestination> = listOf(
            Home,
            AiAssistant,
            Challenges,
            Impact,
            PosterGenerator
        )
    }
}

/**
 * Maps a [Change360Tab] enum to its corresponding type-safe [AppDestination].
 */
fun Change360Tab.toDestination(): AppDestination = when (this) {
    Change360Tab.HOME -> AppDestination.Home
    Change360Tab.AI_COACH -> AppDestination.AiAssistant
    Change360Tab.CHALLENGES -> AppDestination.Challenges
    Change360Tab.IMPACT -> AppDestination.Impact
    Change360Tab.POSTER -> AppDestination.PosterGenerator
}

/**
 * Maps an [AppDestination] to its corresponding [Change360Tab] if it matches a bottom tab,
 * or returns null for root routes like Splash, Auth, and Main.
 */
fun AppDestination.toTab(): Change360Tab? = when (this) {
    AppDestination.Home -> Change360Tab.HOME
    AppDestination.AiAssistant -> Change360Tab.AI_COACH
    AppDestination.Challenges -> Change360Tab.CHALLENGES
    AppDestination.Impact -> Change360Tab.IMPACT
    AppDestination.PosterGenerator -> Change360Tab.POSTER
    else -> null
}

