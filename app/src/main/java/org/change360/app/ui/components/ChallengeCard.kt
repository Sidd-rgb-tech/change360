package org.change360.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.change360.app.domain.model.Challenge
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ChallengeDifficulty
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.MintGlow
import org.change360.app.ui.theme.SolarCyan
import org.change360.app.ui.theme.WarmAmber

/**
 * Reusable Glassmorphic Card representing an active or completed sustainability challenge.
 *
 * Visualizes quest categorization, difficulty stratification, quantified carbon/water/waste savings,
 * animated step progression, and a direct action trigger to advance or complete the challenge.
 *
 * @param challenge The domain model data [Challenge].
 * @param onLogStep Callback executed when the user advances progress by 1 step.
 * @param modifier Layout modifier applied to the card.
 * @param onCardClick Optional callback when the user taps the card body to inspect full details.
 * @param isActionEnabled Whether the progress step button is currently interactive.
 */
@Composable
fun ChallengeCard(
    challenge: Challenge,
    onLogStep: (Challenge) -> Unit,
    modifier: Modifier = Modifier,
    onCardClick: ((Challenge) -> Unit)? = null,
    isActionEnabled: Boolean = true
) {
    val categoryColor = parseColorSafely(challenge.category.accentColorHex, BioLuminescentEmerald)
    val difficultyColor = parseColorSafely(challenge.difficulty.colorHex, WarmAmber)

    val animatedProgress by animateFloatAsState(
        targetValue = challenge.progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "ChallengeProgress"
    )

    GlassmorphicCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onCardClick?.let { { it(challenge) } }
    ) {
        // 1. Header Badges: Category & Difficulty
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Badge
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f))
                    .border(1.dp, categoryColor.copy(alpha = 0.35f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = challenge.category.emoji,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = challenge.category.displayName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = categoryColor
                )
            }

            // Difficulty Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(difficultyColor.copy(alpha = 0.12f))
                    .border(0.8.dp, difficultyColor.copy(alpha = 0.30f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = challenge.difficulty.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = difficultyColor
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Quest Title & Description
        Text(
            text = challenge.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = challenge.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Environmental Impact Pills Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // CO2 Metric Pill
            if (challenge.co2SavedKg > 0f) {
                ImpactMetricPill(
                    icon = Icons.Default.EnergySavingsLeaf,
                    label = "-${challenge.co2SavedKg} kg CO₂",
                    accentColor = BioLuminescentEmerald
                )
            }

            // XP Reward Pill
            ImpactMetricPill(
                icon = Icons.Default.Star,
                label = "+${challenge.xpReward} XP",
                accentColor = WarmAmber
            )

            // Optional Water preservation pill
            if (challenge.waterSavedLiters > 0f) {
                ImpactMetricPill(
                    icon = null,
                    label = "-${challenge.waterSavedLiters.toInt()} L H₂O",
                    accentColor = SolarCyan
                )
            }

            // Optional Landfill waste reduction pill
            if (challenge.wasteSavedKg > 0f) {
                ImpactMetricPill(
                    icon = null,
                    label = "-${challenge.wasteSavedKg} kg Waste",
                    accentColor = MintGlow
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Multi-Step Progress Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Step ${challenge.completedSteps} of ${challenge.totalSteps}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (challenge.isCompleted) BioLuminescentEmerald else SolarCyan
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Smooth Custom Animated Progress Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BioLuminescentEmerald.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                BioLuminescentEmerald,
                                SolarCyan
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Action Footer Button
        if (challenge.isCompleted) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BioLuminescentEmerald.copy(alpha = 0.15f))
                    .border(1.dp, BioLuminescentEmerald.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Challenge Complete",
                    tint = BioLuminescentEmerald,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quest Completed!",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = BioLuminescentEmerald
                )
            }
        } else {
            Button(
                onClick = { onLogStep(challenge) },
                enabled = isActionEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BioLuminescentEmerald,
                    contentColor = Color(0xFF003918),
                    disabledContainerColor = BioLuminescentEmerald.copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Log Step",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                val buttonText = if (challenge.remainingSteps == 1) {
                    "Complete Quest (+${challenge.xpReward} XP)"
                } else {
                    "Log Step (${challenge.completedSteps + 1}/${challenge.totalSteps})"
                }
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/**
 * Small pill badge displaying individual quantified eco-rewards.
 */
@Composable
private fun ImpactMetricPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    label: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.10f))
            .border(0.6.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = accentColor
        )
    }
}

/**
 * Safely parses hex colors without crashing when hex tokens omit '#' or have format deviations.
 */
private fun parseColorSafely(hex: String?, fallback: Color): Color {
    if (hex.isNullOrBlank()) return fallback
    return try {
        val sanitized = if (hex.startsWith("#")) hex else "#$hex"
        Color(android.graphics.Color.parseColor(sanitized))
    } catch (_: Throwable) {
        fallback
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun ChallengeCardPreview() {
    Change360Theme(darkTheme = true) {
        val sampleChallenge = Challenge(
            id = "quest_energy_01",
            title = "Phantom Load Hunt",
            description = "Audit and unplug 5 standby electronic appliances to eliminate vampire electricity draw.",
            category = ChallengeCategory.ENERGY,
            difficulty = ChallengeDifficulty.EASY,
            xpReward = 150,
            co2SavedKg = 2.4f,
            waterSavedLiters = 0f,
            wasteSavedKg = 0f,
            totalSteps = 5,
            completedSteps = 3,
            isCompleted = false
        )

        Box(modifier = Modifier.padding(16.dp)) {
            ChallengeCard(
                challenge = sampleChallenge,
                onLogStep = {}
            )
        }
    }
}
