package org.change360.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.change360.app.ui.theme.AquaTeal
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.DeepForestObsidian
import org.change360.app.ui.theme.SolarCyan
import kotlin.math.min

/**
 * 3-ring breathing radial gradient canvas with scale and fade animations.
 *
 * Implements the signature CHANGE360 planetary bio-luminescent aura effect.
 * Renders three concentric, pulsating radial energy rings (Bio-Luminescent Emerald,
 * Aqua Teal, Solar Cyan) using [BlendMode.Screen] and smooth easing transitions.
 *
 * @param modifier Layout modifier applied to the canvas/container.
 * @param ringColors List of 3 colors for the concentric rings: Inner, Middle, Outer.
 * @param cycleDurationMillis Duration of one full breathing cycle (inhale + exhale) in ms.
 * @param minScale Normalized starting radius scale factor (relative to canvas radius).
 * @param maxScale Normalized peak expansion radius scale factor.
 * @param isBreathing Whether the pulsing animation is actively running.
 * @param blendMode Canvas blend mode for additive bio-luminescent bloom (defaults to Screen).
 * @param content Optional composable overlay positioned at the exact center of the aura.
 */
@Composable
fun AuraPulseCanvas(
    modifier: Modifier = Modifier,
    ringColors: List<Color> = listOf(
        BioLuminescentEmerald,
        AquaTeal,
        SolarCyan
    ),
    cycleDurationMillis: Int = 3000,
    minScale: Float = 0.40f,
    maxScale: Float = 1.15f,
    isBreathing: Boolean = true,
    blendMode: BlendMode = BlendMode.Screen,
    content: (@Composable BoxScope.() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AuraPulseTransition")

    // Primary Ring 1 (Inner - Bio-Luminescent Emerald)
    val ring1Scale by infiniteTransition.animateFloat(
        initialValue = if (isBreathing) minScale else (minScale + maxScale) / 2f,
        targetValue = if (isBreathing) maxScale else (minScale + maxScale) / 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = cycleDurationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ring1Scale"
    )
    val ring1Alpha by infiniteTransition.animateFloat(
        initialValue = if (isBreathing) 0.85f else 0.6f,
        targetValue = if (isBreathing) 0.30f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = cycleDurationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ring1Alpha"
    )

    // Secondary Ring 2 (Middle - Aqua Teal, slightly delayed/offset)
    val ring2Scale by infiniteTransition.animateFloat(
        initialValue = if (isBreathing) minScale * 0.85f else (minScale + maxScale) * 0.45f,
        targetValue = if (isBreathing) maxScale * 1.05f else (minScale + maxScale) * 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (cycleDurationMillis * 1.15f).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ring2Scale"
    )
    val ring2Alpha by infiniteTransition.animateFloat(
        initialValue = if (isBreathing) 0.65f else 0.45f,
        targetValue = if (isBreathing) 0.20f else 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (cycleDurationMillis * 1.15f).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ring2Alpha"
    )

    // Tertiary Ring 3 (Outer - Solar Cyan, deep atmospheric reach)
    val ring3Scale by infiniteTransition.animateFloat(
        initialValue = if (isBreathing) minScale * 0.70f else (minScale + maxScale) * 0.5f,
        targetValue = if (isBreathing) maxScale * 1.20f else (minScale + maxScale) * 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (cycleDurationMillis * 1.30f).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ring3Scale"
    )
    val ring3Alpha by infiniteTransition.animateFloat(
        initialValue = if (isBreathing) 0.45f else 0.35f,
        targetValue = if (isBreathing) 0.12f else 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (cycleDurationMillis * 1.30f).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ring3Alpha"
    )

    // Subtle continuous orbital shimmer rotation (0 to 360 deg)
    val shimmerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AuraShimmerRotation"
    )

    val innerColor = ringColors.getOrElse(0) { BioLuminescentEmerald }
    val middleColor = ringColors.getOrElse(1) { AquaTeal }
    val outerColor = ringColors.getOrElse(2) { SolarCyan }

    Box(
        modifier = modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = min(size.width, size.height) / 2f

            if (baseRadius <= 0f) return@Canvas

            // 1. Draw Outer Ring (Solar Cyan atmosphere)
            drawAuraRing(
                center = centerOffset,
                radius = baseRadius * ring3Scale,
                color = outerColor,
                peakAlpha = ring3Alpha,
                blendMode = blendMode
            )

            // 2. Draw Middle Ring (Aqua Teal bridge)
            drawAuraRing(
                center = centerOffset,
                radius = baseRadius * ring2Scale,
                color = middleColor,
                peakAlpha = ring2Alpha,
                blendMode = blendMode
            )

            // 3. Draw Inner Ring (Emerald bio-core)
            drawAuraRing(
                center = centerOffset,
                radius = baseRadius * ring1Scale,
                color = innerColor,
                peakAlpha = ring1Alpha,
                blendMode = blendMode
            )

            // 4. Subtle orbital boundary guide stroke with rotation shimmer
            val orbitRadius = baseRadius * 0.88f * (ring1Scale / maxScale).coerceIn(0.7f, 1.0f)
            drawCircle(
                color = innerColor.copy(alpha = 0.20f * ring1Alpha),
                radius = orbitRadius,
                center = centerOffset,
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 16f), phase = shimmerRotation)
                )
            )
        }

        // Optional Centered Emblem or Content Slot
        content?.invoke(this)
    }
}

/**
 * Helper to render an individual radial gradient aura wave.
 */
private fun DrawScope.drawAuraRing(
    center: Offset,
    radius: Float,
    color: Color,
    peakAlpha: Float,
    blendMode: BlendMode
) {
    if (radius <= 0f) return
    val radialBrush = Brush.radialGradient(
        colorStops = arrayOf(
            0.0f to color.copy(alpha = peakAlpha),
            0.45f to color.copy(alpha = peakAlpha * 0.55f),
            0.75f to color.copy(alpha = peakAlpha * 0.18f),
            1.0f to Color.Transparent
        ),
        center = center,
        radius = radius
    )

    drawCircle(
        brush = radialBrush,
        radius = radius,
        center = center,
        blendMode = blendMode
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun AuraPulseCanvasPreview() {
    Change360Theme(darkTheme = true) {
        Box(
            modifier = Modifier.size(320.dp),
            contentAlignment = Alignment.Center
        ) {
            AuraPulseCanvas(
                modifier = Modifier.fillMaxSize(),
                cycleDurationMillis = 3000
            )
        }
    }
}
