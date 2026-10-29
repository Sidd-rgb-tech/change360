package org.change360.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.DeepForestObsidian
import org.change360.app.ui.theme.SolarCyan

/**
 * Top-level navigation destinations corresponding to the 5 core tabs of CHANGE360.
 */
enum class Change360Tab(
    val route: String,
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val contentDescription: String
) {
    HOME(
        route = "home",
        title = "Home",
        unselectedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        contentDescription = "Planetary Home Dashboard"
    ),
    AI_COACH(
        route = "ai_assistant",
        title = "AI Coach",
        unselectedIcon = Icons.Outlined.AutoAwesome,
        selectedIcon = Icons.Filled.AutoAwesome,
        contentDescription = "Eco-AI Assistant"
    ),
    CHALLENGES(
        route = "challenges",
        title = "Challenges",
        unselectedIcon = Icons.Outlined.Eco,
        selectedIcon = Icons.Filled.Eco,
        contentDescription = "Eco-Action Quests"
    ),
    IMPACT(
        route = "impact",
        title = "Impact",
        unselectedIcon = Icons.Outlined.Insights,
        selectedIcon = Icons.Filled.Insights,
        contentDescription = "Carbon & Resource Impact Tracker"
    ),
    POSTER(
        route = "poster",
        title = "Posters",
        unselectedIcon = Icons.Outlined.Palette,
        selectedIcon = Icons.Filled.Palette,
        contentDescription = "Advocacy Poster Studio"
    );

    companion object {
        /**
         * Resolves tab from route string matching either exact token or prefixed subpath.
         */
        fun fromRoute(route: String?): Change360Tab {
            if (route == null) return HOME
            return entries.firstOrNull { tab ->
                route.startsWith(tab.route, ignoreCase = true)
            } ?: HOME
        }
    }
}

/**
 * Material 3 Persistent Bottom Navigation Bar for CHANGE360.
 *
 * Hosts the 5 core tabs: Home, AI Coach, Challenges, Impact, and Posters.
 * Implements edge-to-edge window insets, bio-luminescent active pill indicators,
 * and translucent obsidian surface styling.
 *
 * @param selectedTab The currently active [Change360Tab].
 * @param onTabSelected Callback invoked when a tab item is clicked.
 * @param modifier Layout modifier applied to the navigation bar.
 * @param windowInsets Navigation bar insets for edge-to-edge handling.
 */
@Composable
fun Change360BottomBar(
    selectedTab: Change360Tab,
    onTabSelected: (Change360Tab) -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Glowing hairline top divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BioLuminescentEmerald.copy(alpha = 0.35f),
                            SolarCyan.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        NavigationBar(
            containerColor = DeepForestObsidian.copy(alpha = 0.95f),
            windowInsets = windowInsets,
            tonalElevation = 0.dp
        ) {
            Change360Tab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.contentDescription
                        )
                    },
                    label = {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BioLuminescentEmerald,
                        selectedTextColor = BioLuminescentEmerald,
                        indicatorColor = BioLuminescentEmerald.copy(alpha = 0.20f),
                        unselectedIconColor = Color(0xFF7E978C),
                        unselectedTextColor = Color(0xFF7E978C)
                    )
                )
            }
        }
    }
}

/**
 * Route-string overload for seamless integration with Navigation Compose destinations.
 */
@Composable
fun Change360BottomBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets
) {
    val activeTab = Change360Tab.fromRoute(currentRoute)
    Change360BottomBar(
        selectedTab = activeTab,
        onTabSelected = { tab -> onNavigateToRoute(tab.route) },
        modifier = modifier,
        windowInsets = windowInsets
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun Change360BottomBarPreview() {
    Change360Theme(darkTheme = true) {
        var selectedTab by remember { mutableStateOf(Change360Tab.HOME) }
        Change360BottomBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}

