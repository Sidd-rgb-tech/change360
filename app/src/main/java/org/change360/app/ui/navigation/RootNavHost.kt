package org.change360.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.change360.app.Change360Application
import org.change360.app.di.AppContainer
import org.change360.app.ui.screens.auth.AuthScreen
import org.change360.app.ui.screens.splash.SplashScreen

/**
 * Root Navigation Host managing top-level application transitions:
 *
 * ```
 * RootNavHost (startDestination = AppDestination.Splash)
 *  ├── AppDestination.Splash (3s Aura Animation + Auth State Resolution)
 *  │     ├─[Unauthenticated]──► AppDestination.Auth (popUpTo Splash inclusive)
 *  │     └─[Authenticated]────► AppDestination.Main (popUpTo Splash inclusive)
 *  │
 *  ├── AppDestination.Auth (Sign In / Sign Up / Quick Eco-Pledge)
 *  │     └─[Success]──────────► AppDestination.Main (popUpTo Auth inclusive)
 *  │
 *  └── AppDestination.Main (Hosts MainScaffold + Persistent BottomBar)
 *        └─[Sign Out]────────► AppDestination.Auth (popUpTo Main inclusive)
 * ```
 *
 * Implements strict, leak-free backstack handling ensuring previous root destinations
 * are cleanly evicted from the navigation stack upon routing.
 *
 * @param modifier Layout modifier applied to the NavHost.
 * @param navController Top-level navigation controller.
 * @param appContainer Dependency injection container providing domain use cases.
 */
@Composable
fun RootNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    appContainer: AppContainer = Change360Application.appContainer
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash,
        modifier = modifier.fillMaxSize()
    ) {
        // --- 1. Splash Screen Destination ---
        composable<AppDestination.Splash> {
            SplashScreen(
                appContainer = appContainer,
                onNavigateToTarget = { target ->
                    navController.navigate(target) {
                        popUpTo<AppDestination.Splash> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // --- 2. Auth Screen Destination ---
        composable<AppDestination.Auth> {
            AuthScreen(
                appContainer = appContainer,
                onAuthSuccess = {
                    navController.navigate(AppDestination.Main) {
                        popUpTo<AppDestination.Auth> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // --- 3. Main Dashboard Destination ---
        composable<AppDestination.Main> {
            val coroutineScope = rememberCoroutineScope()
            MainScaffold(
                appContainer = appContainer,
                onSignOut = {
                    coroutineScope.launch {
                        appContainer.authenticateUserUseCase.signOut()
                        navController.navigate(AppDestination.Auth) {
                            popUpTo<AppDestination.Main> { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
