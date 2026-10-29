package org.change360.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Root theme composable for CHANGE360.
 *
 * Defaults to the signature Deep Forest Obsidian dark palette with Bio-Luminescent Emerald accents.
 * Dynamic color can optionally be enabled on Android 12+ (API 31+), though the default
 * preserves the tailored environmental brand tokens.
 *
 * @param darkTheme Whether to render using the dark color scheme. Defaults to true.
 * @param dynamicColor Whether to use dynamic theming on Android 12+. Defaults to false.
 * @param content The composable UI tree to wrap.
 */
@Composable
fun Change360Theme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> Change360DarkColorScheme
        else -> Change360LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Configure status and navigation bar icons for edge-to-edge rendering
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Change360Typography,
        shapes = Change360Shapes,
        content = content
    )
}
