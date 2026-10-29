package org.change360.app.ui.screens.poster

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import org.change360.app.Change360Application
import org.change360.app.di.AppContainer
import org.change360.app.di.Change360ViewModelFactory

/**
 * Advocacy Poster Studio composable alias for [PosterScreen].
 *
 * Implements the naming scheme specified in PLAN.md.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosterGeneratorScreen(
    modifier: Modifier = Modifier,
    appContainer: AppContainer = Change360Application.appContainer,
    viewModel: PosterViewModel = viewModel(factory = Change360ViewModelFactory(appContainer))
) {
    PosterScreen(
        modifier = modifier,
        appContainer = appContainer,
        viewModel = viewModel
    )
}

