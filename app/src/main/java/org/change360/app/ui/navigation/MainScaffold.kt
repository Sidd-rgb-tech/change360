package org.change360.app.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.change360.app.Change360Application
import org.change360.app.di.AppContainer
import org.change360.app.ui.components.Change360BottomBar
import org.change360.app.ui.components.Change360Tab
import org.change360.app.ui.screens.ai.AiAssistantScreen
import org.change360.app.ui.screens.challenges.ChallengesScreen
import org.change360.app.ui.screens.home.HomeScreen
import org.change360.app.ui.screens.impact.ImpactScreen
import org.change360.app.ui.screens.poster.PosterScreen

/**
 * Hosts the persistent [Change360BottomBar] and nested [NavHost] for the 5 core planetary tabs:
 * 1. [AppDestination.Home]
 * 2. [AppDestination.AiAssistant]
 * 3. [AppDestination.Challenges]
 * 4. [AppDestination.Impact]
 * 5. [AppDestination.PosterGenerator]
 *
 * Implements type-safe destination switching with state preservation:
 * ```kotlin
 * navController.navigate(destination) {
 *     popUpTo(navController.graph.findStartDestination().id) {
 *         saveState = true
 *     }
 *     launchSingleTop = true
 *     restoreState = true
 * }
 * ```
 *
 * @param modifier Layout modifier applied to the scaffold.
 * @param navController Navigation controller managing the nested tab backstack.
 * @param onSignOut Optional callback triggered when practitioner signs out from dashboard.
 * @param appContainer Dependency injection container providing domain services.
 */
@Composable
fun MainScaffold(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onSignOut: (() -> Unit)? = null,
    appContainer: AppContainer = Change360Application.appContainer
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine the active tab using type-safe destination hierarchy checks
    val currentTab = when {
        currentDestination?.hierarchy?.any { it.hasRoute<AppDestination.Home>() } == true -> Change360Tab.HOME
        currentDestination?.hierarchy?.any { it.hasRoute<AppDestination.AiAssistant>() } == true -> Change360Tab.AI_COACH
        currentDestination?.hierarchy?.any { it.hasRoute<AppDestination.Challenges>() } == true -> Change360Tab.CHALLENGES
        currentDestination?.hierarchy?.any { it.hasRoute<AppDestination.Impact>() } == true -> Change360Tab.IMPACT
        currentDestination?.hierarchy?.any { it.hasRoute<AppDestination.PosterGenerator>() } == true -> Change360Tab.POSTER
        else -> Change360Tab.HOME
    }

    val navigateToTab: (AppDestination) -> Unit = { destination ->
        navController.navigate(destination) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Change360BottomBar(
                selectedTab = currentTab,
                onTabSelected = { tab ->
                    navigateToTab(tab.toDestination())
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Home,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable<AppDestination.Home> {
                HomeScreen(
                    appContainer = appContainer,
                    onNavigateToAi = { navigateToTab(AppDestination.AiAssistant) },
                    onNavigateToPoster = { navigateToTab(AppDestination.PosterGenerator) },
                    onNavigateToChallenges = { navigateToTab(AppDestination.Challenges) },
                    onSignOut = onSignOut
                )
            }

            composable<AppDestination.AiAssistant> {
                AiAssistantScreen(appContainer = appContainer)
            }

            composable<AppDestination.Challenges> {
                ChallengesScreen(appContainer = appContainer)
            }

            composable<AppDestination.Impact> {
                ImpactScreen(appContainer = appContainer)
            }

            composable<AppDestination.PosterGenerator> {
                PosterScreen(appContainer = appContainer)
            }
        }
    }
}
