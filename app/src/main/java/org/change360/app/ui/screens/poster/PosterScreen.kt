package org.change360.app.ui.screens.poster

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
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
import org.change360.app.domain.model.PosterAuraStyle
import org.change360.app.domain.model.PosterTemplate
import org.change360.app.domain.model.SavedPoster
import org.change360.app.ui.components.Change360TopBar
import org.change360.app.ui.components.GlassmorphicCard
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.CoralAlert
import org.change360.app.ui.theme.DeepForestObsidian
import org.change360.app.ui.theme.SolarCyan

/**
 * Stateful entry composable for the Advocacy Poster Studio screen.
 *
 * Connects to [PosterViewModel] via [Change360ViewModelFactory], collects [PosterUiState]
 * with lifecycle awareness ([collectAsStateWithLifecycle]), and delegates to [PosterContent].
 *
 * @param modifier Layout modifier applied to the root container.
 * @param appContainer Dependency injection container providing application singletons.
 * @param viewModel ViewModel governing poster customization, template selection, and export pipelines.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosterScreen(
    modifier: Modifier = Modifier,
    appContainer: AppContainer = Change360Application.appContainer,
    viewModel: PosterViewModel = viewModel(factory = Change360ViewModelFactory(appContainer))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    PosterContent(
        uiState = uiState,
        onStyleSelected = viewModel::onStyleSelected,
        onHeadlineChanged = viewModel::onHeadlineChanged,
        onQuoteChanged = viewModel::onQuoteChanged,
        onIncludeStatsBadgeToggled = viewModel::onIncludeStatsBadgeToggled,
        onApplyTemplate = viewModel::applyTemplate,
        onSavePoster = { viewModel.savePoster() },
        onSharePoster = { sharePoster(context, uiState) },
        onDeleteSavedPoster = viewModel::deleteSavedPoster,
        onClearError = viewModel::clearError,
        modifier = modifier
    )
}

/**
 * Stateless presentation composable for advocacy poster customization and live preview.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosterContent(
    uiState: PosterUiState,
    onStyleSelected: (PosterAuraStyle) -> Unit,
    onHeadlineChanged: (String) -> Unit,
    onQuoteChanged: (String) -> Unit,
    onIncludeStatsBadgeToggled: (Boolean) -> Unit,
    onApplyTemplate: (PosterTemplate) -> Unit,
    onSavePoster: () -> Unit,
    onSharePoster: () -> Unit,
    onDeleteSavedPoster: (Long) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val style = uiState.selectedStyle
    val primaryColor = parseColorSafely(style.primaryColorHex, BioLuminescentEmerald)
    val secondaryColor = parseColorSafely(style.secondaryColorHex, SolarCyan)
    val backgroundColor = parseColorSafely(style.backgroundHex, DeepForestObsidian)
    val verifiedBadge = uiState.verifiedBadge

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Change360TopBar(
            title = "Advocacy Poster Studio",
            subtitle = "Visual Planetary Affirmation"
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

            // 1. Live 4:5 Social Poster Card Preview
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                shape = RoundedCornerShape(20.dp),
                color = backgroundColor,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Brush.verticalGradient(
                        listOf(primaryColor, secondaryColor.copy(alpha = 0.45f))
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Header / Branding
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = primaryColor.copy(alpha = 0.20f),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.EnergySavingsLeaf,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CHANGE360",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp
                                    ),
                                    color = Color.White
                                )
                            }

                            if (uiState.includeStatsBadge && verifiedBadge != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Black.copy(alpha = 0.50f),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, primaryColor.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "⚡ Level ${verifiedBadge.auraLevel} • ${verifiedBadge.auraTitle}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = primaryColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Central Advocacy Affirmation & Pledge Quote
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = uiState.headline.ifBlank { "Planetary Guardian" },
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    lineHeight = 32.sp
                                ),
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "\"${uiState.pledgeQuote.ifBlank { "For a regenerative Earth." }}\"",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 22.sp
                                ),
                                color = Color.White.copy(alpha = 0.90f),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Footer / Verified Planetary Impact
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "VERIFIED IMPACT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = primaryColor
                                )
                                Text(
                                    text = "%.1f kg CO₂ Avoided • ${verifiedBadge?.streakDays ?: 1}-Day Streak".format(verifiedBadge?.co2SavedKg ?: 0f),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.80f)
                                )
                            }
                            Text(
                                text = "org.change360.app",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color.White.copy(alpha = 0.50f)
                            )
                        }
                    }
                }
            }

            // 2. Starter Poster Templates
            if (uiState.templates.isNotEmpty()) {
                Text(
                    text = "Curated Template Inspirations",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.templates) { template ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            border = androidx.compose.foundation.BorderStroke(1.dp, parseColorSafely(template.accentColorHex, BioLuminescentEmerald).copy(alpha = 0.35f)),
                            modifier = Modifier.clickable { onApplyTemplate(template) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(parseColorSafely(template.accentColorHex, BioLuminescentEmerald))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = template.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 3. Aura Gradient Palette Selector
            Text(
                text = "Select Aura Palette",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PosterAuraStyle.entries) { auraStyle ->
                    val isSelected = auraStyle == uiState.selectedStyle
                    val chipColor = parseColorSafely(auraStyle.primaryColorHex, BioLuminescentEmerald)

                    FilterChip(
                        selected = isSelected,
                        onClick = { onStyleSelected(auraStyle) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(chipColor)
                            )
                        },
                        label = {
                            Text(
                                text = auraStyle.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor.copy(alpha = 0.22f),
                            selectedLabelColor = chipColor
                        )
                    )
                }
            }

            // 4. Input Fields
            OutlinedTextField(
                value = uiState.headline,
                onValueChange = onHeadlineChanged,
                label = { Text("Advocacy Headline") },
                placeholder = { Text("Act Today For Tomorrow") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    cursorColor = primaryColor
                )
            )

            OutlinedTextField(
                value = uiState.pledgeQuote,
                onValueChange = onQuoteChanged,
                label = { Text("Pledge Quote Affirmation") },
                placeholder = { Text("Every small habit shift regenerates our collective biosphere.") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    cursorColor = primaryColor
                )
            )

            // 5. Include Verified Stats Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Include Verified Stats Badge",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Embeds live streak days and verified carbon savings",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.includeStatsBadge,
                    onCheckedChange = onIncludeStatsBadgeToggled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = primaryColor,
                        checkedTrackColor = primaryColor.copy(alpha = 0.35f)
                    )
                )
            }

            // 6. Action Control Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onSavePoster,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = primaryColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save Design",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = primaryColor
                        )
                    }
                }

                Button(
                    onClick = onSharePoster,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                        tint = DeepForestObsidian,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share Poster",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = DeepForestObsidian
                    )
                }
            }

            // 7. Locally Saved Posters Gallery
            if (uiState.savedPosters.isNotEmpty()) {
                Text(
                    text = "Saved Posters (${uiState.savedPosters.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.savedPosters.forEach { poster ->
                        SavedPosterRow(
                            poster = poster,
                            onDelete = { onDeleteSavedPoster(poster.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SavedPosterRow(
    poster: SavedPoster,
    onDelete: () -> Unit
) {
    val styleColor = parseColorSafely(poster.accentColorHex, BioLuminescentEmerald)

    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(styleColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = poster.title,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "\"${poster.pledgeQuote}\"",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete saved poster",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Dispatches an Android Share Sheet intent containing the formatted advocacy poster message.
 */
private fun sharePoster(context: Context, uiState: PosterUiState) {
    val badge = uiState.verifiedBadge
    val statsLine = if (uiState.includeStatsBadge && badge != null) {
        "⚡ Verified Impact: %.1f kg CO₂ mitigated • %d-Day Streak".format(badge.co2SavedKg, badge.streakDays)
    } else {
        "⚡ Verified on CHANGE360 Planetary Movement"
    }

    val shareText = """
        🌍 CHANGE360 Advocacy Affirmation
        
        "${uiState.headline.ifBlank { "Planetary Guardian" }}"
        "${uiState.pledgeQuote.ifBlank { "For a regenerative Earth." }}"
        
        $statsLine
        Join the movement for 360° planetary regeneration!
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "My CHANGE360 Advocacy Affirmation")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Advocacy Affirmation"))
}

private fun parseColorSafely(hex: String?, fallback: Color): Color {
    if (hex.isNullOrBlank()) return fallback
    return try {
        val sanitized = if (hex.startsWith("#")) hex else "#$hex"
        Color(android.graphics.Color.parseColor(sanitized))
    } catch (_: Throwable) {
        fallback
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun PosterScreenPreview() {
    Change360Theme(darkTheme = true) {
        PosterContent(
            uiState = PosterUiState(),
            onStyleSelected = {},
            onHeadlineChanged = {},
            onQuoteChanged = {},
            onIncludeStatsBadgeToggled = {},
            onApplyTemplate = {},
            onSavePoster = {},
            onSharePoster = {},
            onDeleteSavedPoster = {},
            onClearError = {}
        )
    }
}

