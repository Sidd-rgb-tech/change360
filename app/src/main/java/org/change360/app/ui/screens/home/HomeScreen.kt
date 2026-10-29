package org.change360.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.change360.app.Change360Application
import org.change360.app.di.AppContainer
import org.change360.app.di.Change360ViewModelFactory
import org.change360.app.domain.model.ChallengeStepResult
import org.change360.app.ui.components.AuraPulseCanvas
import org.change360.app.ui.components.ChallengeCard
import org.change360.app.ui.components.Change360TopBar
import org.change360.app.ui.components.GlassmorphicCard
import org.change360.app.ui.components.ImpactMetricRing
import org.change360.app.ui.theme.AquaTeal
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.CoralAlert
import org.change360.app.ui.theme.DeepForestObsidian
import org.change360.app.ui.theme.SolarCyan
import org.change360.app.ui.theme.WarmAmber

/**
 * Stateful entry composable for the CHANGE360 Planetary Home Dashboard.
 *
 * Connects to [HomeViewModel] via [Change360ViewModelFactory], collects [HomeUiState]
 * with lifecycle awareness ([collectAsStateWithLifecycle]), and coordinates quest
 * step progressions and tab navigation shortcuts.
 *
 * @param onNavigateToAi Callback navigating to the conversational AI Coach tab.
 * @param onNavigateToPoster Callback navigating to the Advocacy Poster Studio tab.
 * @param onNavigateToChallenges Callback navigating to the Quests tab.
 * @param modifier Layout modifier applied to the root container.
 * @param onSignOut Optional callback to sign out of the practitioner session.
 * @param appContainer Dependency injection container providing application singletons.
 * @param viewModel ViewModel governing dashboard state aggregation and quest updates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAi: () -> Unit,
    onNavigateToPoster: () -> Unit,
    onNavigateToChallenges: () -> Unit,
    modifier: Modifier = Modifier,
    onSignOut: (() -> Unit)? = null,
    appContainer: AppContainer = Change360Application.appContainer,
    viewModel: HomeViewModel = viewModel(factory = Change360ViewModelFactory(appContainer))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onLogQuestStep = viewModel::logQuestStep,
        onClearCelebration = viewModel::clearCelebration,
        onClearError = viewModel::clearError,
        onNavigateToAi = onNavigateToAi,
        onNavigateToPoster = onNavigateToPoster,
        onNavigateToChallenges = onNavigateToChallenges,
        onSignOut = onSignOut,
        modifier = modifier
    )
}

/**
 * Stateless presentation composable for the Home dashboard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onLogQuestStep: (String) -> Unit,
    onClearCelebration: () -> Unit,
    onClearError: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToPoster: () -> Unit,
    onNavigateToChallenges: () -> Unit,
    onSignOut: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val overview = uiState.overview
    val user = overview.user
    val today = overview.todayImpact
    val activeQuests = overview.activeChallenges

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Change360TopBar(
            title = "CHANGE360",
            subtitle = "Planetary Regeneration Movement",
            actions = {
                if (onSignOut != null) {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sign Out",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Notification Banner
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CoralAlert.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CoralAlert.copy(alpha = 0.40f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = CoralAlert,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onClearError,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Dismiss error",
                                tint = CoralAlert,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // 1. Practitioner Greeting & Streak Banner
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello, ${user?.displayName?.ifBlank { "Earth Guardian" } ?: "Earth Guardian"}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = user?.auraTitle ?: "Eco Seedling",
                            style = MaterialTheme.typography.bodySmall,
                            color = BioLuminescentEmerald
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = WarmAmber.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Whatshot,
                                contentDescription = "Active Streak",
                                tint = WarmAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${user?.currentStreakDays ?: 0} Days",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = WarmAmber
                            )
                        }
                    }
                }
            }

            // 2. Central Aura Pulse & Today's Impact Ring
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PLANETARY AURA ENERGY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier.size(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AuraPulseCanvas(
                            modifier = Modifier.fillMaxSize(),
                            ringColors = listOf(BioLuminescentEmerald, AquaTeal, SolarCyan)
                        )
                        ImpactMetricRing(
                            progress = user?.progressInCurrentLevel ?: 0.35f,
                            primaryValue = "%.1f".format(today.totalCo2SavedKg),
                            metricLabel = "KG CO₂ TODAY",
                            unitLabel = "kg",
                            size = 140.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Level ${user?.auraLevel ?: 1} • ${user?.xpRemainingForNextLevel ?: 500} XP to Next Tier",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3. Today's Metric Snapshot Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SnapshotMetricPill(
                    label = "CO₂ Saved",
                    value = "%.1f kg".format(today.totalCo2SavedKg),
                    color = BioLuminescentEmerald,
                    modifier = Modifier.weight(1f)
                )
                SnapshotMetricPill(
                    label = "Water Saved",
                    value = "%.1f L".format(today.totalWaterSavedLiters),
                    color = SolarCyan,
                    modifier = Modifier.weight(1f)
                )
                SnapshotMetricPill(
                    label = "Waste Diverted",
                    value = "%.1f kg".format(today.totalWasteSavedKg),
                    color = WarmAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            // 4. Quick Launch Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickLaunchCard(
                    title = "Ask Eco-AI Coach",
                    subtitle = "Get tailored guidance",
                    icon = Icons.Filled.AutoAwesome,
                    color = BioLuminescentEmerald,
                    onClick = onNavigateToAi,
                    modifier = Modifier.weight(1f)
                )
                QuickLaunchCard(
                    title = "Poster Studio",
                    subtitle = "Share your pledge",
                    icon = Icons.Filled.Palette,
                    color = SolarCyan,
                    onClick = onNavigateToPoster,
                    modifier = Modifier.weight(1f)
                )
            }

            // 5. Active Quests Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Quests (${activeQuests.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onNavigateToChallenges) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = BioLuminescentEmerald
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = BioLuminescentEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                if (activeQuests.isEmpty()) {
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNavigateToChallenges)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = BioLuminescentEmerald,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All active quests completed!",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap here to explore and launch new sustainability quests.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    activeQuests.take(3).forEach { challenge ->
                        ChallengeCard(
                            challenge = challenge,
                            onLogStep = { onLogQuestStep(challenge.id) },
                            isActionEnabled = !uiState.isLoggingStep
                        )
                    }
                }
            }

            // 6. Daily Eco Tip Card
            overview.dailyTip?.let { tip ->
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = tip.category.emoji,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Eco Tip: ${tip.title}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tip.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Celebration Modal Dialog
        uiState.celebrationEvent?.let { celebration ->
            CelebrationDialog(
                stepResult = celebration,
                onDismiss = onClearCelebration
            )
        }
    }
}

@Composable
private fun SnapshotMetricPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.10f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.30f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickLaunchCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CelebrationDialog(
    stepResult: ChallengeStepResult,
    onDismiss: () -> Unit
) {
    val challenge = stepResult.updatedChallenge

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepForestObsidian,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = WarmAmber,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quest Completed!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = BioLuminescentEmerald
                )
                Text(
                    text = "You earned +${challenge.xpReward} XP and mitigated ${challenge.co2SavedKg} kg of CO₂ emissions for our planet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BioLuminescentEmerald)
            ) {
                Text(
                    text = "Claim & Continue",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = DeepForestObsidian
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun HomeScreenPreview() {
    Change360Theme(darkTheme = true) {
        HomeContent(
            uiState = HomeUiState(),
            onLogQuestStep = {},
            onClearCelebration = {},
            onClearError = {},
            onNavigateToAi = {},
            onNavigateToPoster = {},
            onNavigateToChallenges = {},
            onSignOut = {}
        )
    }
}

