package org.change360.app.ui.screens.impact

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import org.change360.app.domain.model.CategoryImpactBreakdown
import org.change360.app.domain.model.ImpactLog
import org.change360.app.domain.model.TimeRange
import org.change360.app.ui.components.Change360TopBar
import org.change360.app.ui.components.GlassmorphicCard
import org.change360.app.ui.components.ImpactMetricRing
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.CoralAlert
import org.change360.app.ui.theme.DeepForestObsidian
import org.change360.app.ui.theme.MintGlow
import org.change360.app.ui.theme.SolarCyan
import org.change360.app.ui.theme.WarmAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Stateful entry composable for the Carbon & Resource Impact Accounting screen.
 *
 * Connects to [ImpactViewModel] via [Change360ViewModelFactory], collects [ImpactUiState]
 * with lifecycle awareness ([collectAsStateWithLifecycle]), and delegates to [ImpactContent].
 *
 * @param modifier Layout modifier applied to the root container.
 * @param appContainer Dependency injection container providing application singletons.
 * @param viewModel ViewModel governing impact calculations, time windows, and ledger operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactScreen(
    modifier: Modifier = Modifier,
    appContainer: AppContainer = Change360Application.appContainer,
    viewModel: ImpactViewModel = viewModel(factory = Change360ViewModelFactory(appContainer))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ImpactContent(
        uiState = uiState,
        onTimeRangeSelected = viewModel::onTimeRangeSelected,
        onDeleteLog = viewModel::deleteLog,
        onClearError = viewModel::clearError,
        modifier = modifier
    )
}

/**
 * Stateless presentation composable for environmental impact analytics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactContent(
    uiState: ImpactUiState,
    onTimeRangeSelected: (TimeRange) -> Unit,
    onDeleteLog: (Long) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val analytics = uiState.analytics
    val summary = uiState.summary
    val equivalents = analytics?.equivalents

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Change360TopBar(
            title = "Carbon & Resource Impact",
            subtitle = "Verified Environmental Accounting"
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

            // 1. Time Range Selector Tabs (This Week / This Month / All Time)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimeRange.entries.forEach { range ->
                    val isSelected = range == uiState.timeRange
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) BioLuminescentEmerald else Color.Transparent)
                            .clickable { onTimeRangeSelected(range) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (range) {
                                TimeRange.WEEK -> "This Week"
                                TimeRange.MONTH -> "This Month"
                                TimeRange.ALL_TIME -> "All Time"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) DeepForestObsidian else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // 2. Primary Impact Ring Card
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TOTAL CARBON MITIGATED",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ImpactMetricRing(
                        progress = ((summary.totalCo2SavedKg) / 50f).coerceIn(0.1f, 1f),
                        primaryValue = "%.1f".format(summary.totalCo2SavedKg),
                        metricLabel = "KG CO₂ AVOIDED",
                        unitLabel = "kg",
                        size = 150.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary Metrics Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Water: %.1f L".format(summary.totalWaterSavedLiters),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = SolarCyan
                        )
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Waste: %.1f kg".format(summary.totalWasteSavedKg),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = WarmAmber
                        )
                    }

                    // Weekly Momentum Indicator
                    analytics?.weeklyTrendPercentage?.let { trend ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (trend >= 0f) BioLuminescentEmerald.copy(alpha = 0.15f) else WarmAmber.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = if (trend >= 0f) BioLuminescentEmerald else WarmAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (trend >= 0f) "+%.1f%% momentum vs prior week".format(trend) else "%.1f%% momentum".format(trend),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (trend >= 0f) BioLuminescentEmerald else WarmAmber
                                )
                            }
                        }
                    }
                }
            }

            // 3. Category Breakdown Distribution
            if (summary.categoryBreakdowns.isNotEmpty()) {
                Text(
                    text = "Impact By Category",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        summary.categoryBreakdowns.forEach { breakdown ->
                            CategoryBreakdownRow(breakdown = breakdown)
                        }
                    }
                }
            }

            // 4. Real-World Equivalencies Grid
            Text(
                text = "Planetary Equivalencies",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EquivalencyCard(
                    icon = Icons.Filled.Forest,
                    label = "Mature Trees Equivalent",
                    value = "%.1f".format(equivalents?.treesAbsorbingCo2 ?: (summary.totalCo2SavedKg / 21.77f)),
                    color = BioLuminescentEmerald,
                    modifier = Modifier.weight(1f)
                )
                EquivalencyCard(
                    icon = Icons.Filled.DirectionsCar,
                    label = "Car Km Avoided",
                    value = "%.1f".format(equivalents?.passengerCarKmAvoided ?: (summary.totalCo2SavedKg * 4.1f)),
                    color = SolarCyan,
                    modifier = Modifier.weight(1f)
                )
                EquivalencyCard(
                    icon = Icons.Filled.LocalDrink,
                    label = "Bottles Diverted",
                    value = "${equivalents?.plasticBottlesDiverted ?: (summary.totalWasteSavedKg * 40).toInt()}",
                    color = WarmAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            // Additional Energy Savings Pill Card
            equivalents?.let { eq ->
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = null,
                                tint = WarmAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "%.0f hrs".format(eq.ledBulbHoursEquivalent),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "LED Light Hours",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Bolt,
                                contentDescription = null,
                                tint = SolarCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${eq.smartphoneChargesEquivalent} charges",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Smartphones Powered",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 5. Recent Verified Activity Feed
            Text(
                text = "Recent Verified Activity (${uiState.recentLogs.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (uiState.recentLogs.isEmpty()) {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No eco-actions registered yet for this window. Complete quest steps to record verified planetary impact!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                uiState.recentLogs.forEach { log ->
                    RecentLogItemCard(
                        log = log,
                        onDelete = { onDeleteLog(log.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryBreakdownRow(breakdown: CategoryImpactBreakdown) {
    val percentage = breakdown.percentageOfTotalCo2.coerceIn(0f, 100f)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = breakdown.category.emoji,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = breakdown.category.displayName,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "%.1f kg CO₂ (%.0f%%)".format(breakdown.co2Kg, percentage),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(BioLuminescentEmerald.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(BioLuminescentEmerald, SolarCyan)
                        )
                    )
            )
        }
    }
}

@Composable
private fun EquivalencyCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun RecentLogItemCard(
    log: ImpactLog,
    onDelete: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(log.loggedAtEpochMillis) {
        dateFormatter.format(Date(log.loggedAtEpochMillis))
    }

    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = BioLuminescentEmerald,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.actionTitle,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "-%.2f kg CO₂ • +%.1f L Water • %s".format(log.co2SavedKg, log.waterSavedLiters, formattedDate),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete log entry",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun ImpactScreenPreview() {
    Change360Theme(darkTheme = true) {
        ImpactContent(
            uiState = ImpactUiState(),
            onTimeRangeSelected = {},
            onDeleteLog = {},
            onClearError = {}
        )
    }
}

