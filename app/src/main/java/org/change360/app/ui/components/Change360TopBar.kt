package org.change360.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.DeepForestObsidian
import org.change360.app.ui.theme.SolarCyan

/**
 * Material 3 Top App Bar customized for CHANGE360.
 *
 * Implements edge-to-edge status bar padding with a translucent Deep Forest Obsidian
 * container, subtle bio-luminescent border separator, and flexible navigation/action slots.
 *
 * @param title Top bar headline text.
 * @param modifier Layout modifier applied to the app bar container.
 * @param subtitle Optional secondary subtitle text positioned beneath the title.
 * @param canNavigateBack When true, renders a back navigation arrow calling [onNavigateBack].
 * @param onNavigateBack Callback executed when back arrow icon is pressed.
 * @param navigationIcon Optional custom composable replacing the default navigation slot.
 * @param actions Composable action slot (e.g. streaks, profile, share).
 * @param scrollBehavior Material 3 scroll behavior for pinned/enterAlways collapse animations.
 * @param centerAligned Whether to center-align the title (defaults to true for top-level elegance).
 * @param windowInsets WindowInsets consumed by the bar (defaults to status bars).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Change360TopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    canNavigateBack: Boolean = false,
    onNavigateBack: (() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    centerAligned: Boolean = true,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    val navSlot: @Composable () -> Unit = {
        when {
            navigationIcon != null -> navigationIcon()
            canNavigateBack && onNavigateBack != null -> {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    val titleSlot: @Composable () -> Unit = {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    val barColors = TopAppBarDefaults.topAppBarColors(
        containerColor = DeepForestObsidian.copy(alpha = 0.92f),
        scrolledContainerColor = DeepForestObsidian.copy(alpha = 0.98f),
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Column(modifier = modifier.fillMaxWidth()) {
        if (centerAligned) {
            CenterAlignedTopAppBar(
                title = titleSlot,
                navigationIcon = navSlot,
                actions = actions,
                colors = barColors,
                scrollBehavior = scrollBehavior,
                windowInsets = windowInsets
            )
        } else {
            TopAppBar(
                title = titleSlot,
                navigationIcon = navSlot,
                actions = actions,
                colors = barColors,
                scrollBehavior = scrollBehavior,
                windowInsets = windowInsets
            )
        }

        // Subtle bio-luminescent hairline divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BioLuminescentEmerald.copy(alpha = 0.25f),
                            SolarCyan.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun Change360TopBarPreview() {
    Change360Theme(darkTheme = true) {
        Change360TopBar(
            title = "CHANGE360",
            subtitle = "Regenerative Planetary Action",
            canNavigateBack = true,
            onNavigateBack = {}
        )
    }
}
