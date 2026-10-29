package org.change360.app.ui.screens.challenges

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ChallengeStepResult
import org.change360.app.ui.components.ChallengeCard
import org.change360.app.ui.components.Change360TopBar
import org.change360.app.ui.components.GlassmorphicCard
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.CoralAlert
import org.change360.app.ui.theme.DeepForestObsidian
import org.change360.app.ui.theme.SolarCyan
import org.change360.app.ui.theme.WarmAmber

/**
 * Stateful entry composable for the Eco-Action Quests screen.
 *
 * Connects to [ChallengesViewModel] via [Change360ViewModelFactory], collects [ChallengesUiState]
 * with lifecycle awareness ([collectAsStateWithLifecycle]), and delegates to [ChallengesContent].
 *
 * @param modifier Layout modifier applied to the root container.
 * @param appContainer Dependency injection container providing application singletons.
 * @param viewModel ViewModel governing challenge categorization, step logging, and milestones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    modifier: Modifier = Modifier,
    appContainer: AppContainer = Change360Application.appContainer,
    viewModel: ChallengesViewModel = viewModel(factory = Change360ViewModelFactory(appContainer))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChallengesContent(
        uiState = uiState,
        onCategorySelected = viewModel::onCategorySelected,
        onLogChallengeStep = viewModel::logChallengeStep,
        onClearCelebration = viewModel::clearCelebration,
        onClearError = viewModel::clearError,
        modifier = modifier
    )
}

/**
 * Stateless presentation composable for the sustainability quests screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesContent(
    uiState: ChallengesUiState,
    onCategorySelected: (ChallengeCategory) -> Unit,
    onLogChallengeStep: (String) -> Unit,
    onClearCelebration: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressRatio = if (uiState.totalCount > 0) {
        uiState.completedCount.toFloat() / uiState.totalCount.toFloat()
    } else {
        0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Change360TopBar(
            title = "Eco-Action Quests",
            subtitle = "Active Planetary Stewardship"
        )

        // Error Banner
        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CoralAlert.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, CoralAlert.copy(alpha = 0.40f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
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

        // 1. Milestone Progress Overview Card
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Eco,
                        contentDescription = null,
                        tint = BioLuminescentEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Global Stewardship Progress",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${uiState.completedCount} of ${uiState.totalCount} Done",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = BioLuminescentEmerald
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(BioLuminescentEmerald.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressRatio.coerceIn(0f, 1f))
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(BioLuminescentEmerald, SolarCyan)
                            )
                        )
                )
            }
        }

        // 2. Category Filter Chips Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ChallengeCategory.entries) { category ->
                val isSelected = category == uiState.selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    label = {
                        Text(
                            text = if (category == ChallengeCategory.ALL) "All Quests" else "${category.emoji} ${category.displayName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BioLuminescentEmerald.copy(alpha = 0.22f),
                        selectedLabelColor = BioLuminescentEmerald,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
                    )
                )
            }
        }

        // 3. Quests Feed
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            if (uiState.challenges.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No challenges found for the selected category.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(uiState.challenges, key = { it.id }) { challenge ->
                    ChallengeCard(
                        challenge = challenge,
                        onLogStep = { onLogChallengeStep(challenge.id) },
                        isActionEnabled = !uiState.isLoading
                    )
                }
            }
        }

        // Milestone Victory Celebration Modal
        uiState.celebrationEvent?.let { celebration ->
            CelebrationDialog(
                stepResult = celebration,
                onDismiss = onClearCelebration
            )
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
                    text = "Congratulations! You earned +${challenge.xpReward} XP and mitigated ${challenge.co2SavedKg} kg CO₂ for our planet.",
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
private fun ChallengesScreenPreview() {
    Change360Theme(darkTheme = true) {
        ChallengesContent(
            uiState = ChallengesUiState(),
            onCategorySelected = {},
            onLogChallengeStep = {},
            onClearCelebration = {},
            onClearError = {}
        )
    }
}

