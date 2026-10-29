package org.change360.app.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.change360.app.Change360Application
import org.change360.app.di.AppContainer
import org.change360.app.di.Change360ViewModelFactory
import org.change360.app.ui.components.AuraPulseCanvas
import org.change360.app.ui.navigation.AppDestination
import org.change360.app.ui.theme.AquaTeal
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.DeepForestObsidian
import org.change360.app.ui.theme.SolarCyan

/**
 * Stateful entry composable for the 3-second bio-luminescent splash experience.
 *
 * Connects to [SplashViewModel] via [Change360ViewModelFactory], collects [SplashUiState]
 * with lifecycle awareness ([collectAsStateWithLifecycle]), and dispatches navigation
 * to [onNavigateToTarget] when the aura breathing sequence and credential check conclude.
 *
 * @param onNavigateToTarget Callback invoked with the resolved destination ([AppDestination.Main] or [AppDestination.Auth]).
 * @param modifier Layout modifier applied to the root container.
 * @param appContainer Dependency injection container providing application singletons.
 * @param viewModel ViewModel governing the aura animation timer and session verification.
 */
@Composable
fun SplashScreen(
    onNavigateToTarget: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
    appContainer: AppContainer = Change360Application.appContainer,
    viewModel: SplashViewModel = viewModel(factory = Change360ViewModelFactory(appContainer))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isAuraReady, uiState.nextDestination) {
        val destination = uiState.nextDestination
        if (uiState.isAuraReady && destination != null) {
            onNavigateToTarget(destination)
        }
    }

    SplashContent(modifier = modifier)
}

/**
 * Stateless presentation composable rendering the breathing aura rings and central emblem.
 */
@Composable
fun SplashContent(
    modifier: Modifier = Modifier
) {
    var isLogoVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLogoVisible = true
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DeepForestObsidian
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Concentric 3-ring pulsating radial energy canvas
            AuraPulseCanvas(
                modifier = Modifier.fillMaxSize(),
                ringColors = listOf(
                    BioLuminescentEmerald,
                    AquaTeal,
                    SolarCyan
                ),
                cycleDurationMillis = 3000
            )

            // Central CHANGE360 Emblem & Typography
            AnimatedVisibility(
                visible = isLogoVisible,
                enter = fadeIn(tween(1000)) + scaleIn(tween(1200, easing = FastOutSlowInEasing))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BioLuminescentEmerald.copy(alpha = 0.15f),
                        border = BorderStroke(
                            2.dp,
                            Brush.linearGradient(listOf(BioLuminescentEmerald, SolarCyan))
                        ),
                        modifier = Modifier.size(96.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Public,
                                contentDescription = "CHANGE360 Planetary Logo",
                                tint = BioLuminescentEmerald,
                                modifier = Modifier.size(54.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "CHANGE360",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 4.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "360° Action for a Regenerative Planet",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = SolarCyan,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun SplashScreenPreview() {
    Change360Theme(darkTheme = true) {
        SplashContent()
    }
}

