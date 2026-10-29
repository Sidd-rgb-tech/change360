package org.change360.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.change360.app.ui.theme.AquaTeal
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.SolarCyan
import kotlin.math.cos
import kotlin.math.sin

/**
 * Circular Bio-Luminescent Progress Ring and Metric Indicator.
 *
 * Renders an animated circular sweep gauge displaying environmental impact stats,
 * Aura Energy progress, or milestone completion percentages.
 *
 * @param progress Normalized progress value between 0.0f and 1.0f.
 * @param primaryValue Prominent text displayed at the core of the ring (e.g. "24.5", "Level 5").
 * @param metricLabel Descriptive subtitle text displayed below the value (e.g. "CO₂ SAVED", "AURA XP").
 * @param modifier Layout modifier applied to the ring container.
 * @param unitLabel Optional trailing unit string (e.g. "kg", "pts", "%").
 * @param size Outer dimension bounding the circular ring.
 * @param strokeWidth Thickness of the animated sweep arc.
 * @param trackColor Color of the background circle track.
 * @param progressColors Gradient palette mapped along the sweep arc.
 * @param startAngle Angle at which progress starts (-90f corresponds to 12 o'clock).
 * @param maxSweepAngle Total angular span of the ring (360f for closed circle, 270f for open gauge).
 * @param animate Whether progress changes animate smoothly.
 * @param animationDurationMillis Duration of the progress transition in ms.
 * @param centerContent Optional custom composable replacing the default centered metric text.
 */
@Composable
fun ImpactMetricRing(
    progress: Float,
    primaryValue: String,
    metricLabel: String,
    modifier: Modifier = Modifier,
    unitLabel: String? = null,
    size: Dp = 160.dp,
    strokeWidth: Dp = 12.dp,
    trackColor: Color = BioLuminescentEmerald.copy(alpha = 0.12f),
    progressColors: List<Color> = listOf(
        BioLuminescentEmerald,
        AquaTeal,
        SolarCyan
    ),
    startAngle: Float = -90f,
    maxSweepAngle: Float = 360f,
    animate: Boolean = true,
    animationDurationMillis: Int = 1100,
    centerContent: (@Composable BoxScope.() -> Unit)? = null
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = if (animate) clampedProgress else clampedProgress,
        animationSpec = tween(durationMillis = animationDurationMillis, easing = FastOutSlowInEasing),
        label = "MetricRingProgress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            val arcSize = Size(
                width = this.size.width - strokeWidthPx,
                height = this.size.height - strokeWidthPx
            )
            val topLeft = Offset(strokeWidthPx / 2f, strokeWidthPx / 2f)

            // 1. Draw Inactive Background Track Arc
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = maxSweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // 2. Draw Active Gradient Progress Arc
            val currentSweep = maxSweepAngle * animatedProgress
            if (currentSweep > 0f) {
                rotate(degrees = startAngle, pivot = center) {
                    val sweepBrush = Brush.sweepGradient(
                        colors = if (progressColors.size >= 2) progressColors else listOf(progressColors.first(), progressColors.first()),
                        center = center
                    )

                    drawArc(
                        brush = sweepBrush,
                        startAngle = 0f,
                        sweepAngle = currentSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )
                }

                // 3. Draw Glowing Endpoint Accent Dot
                val endAngleRad = Math.toRadians((startAngle + currentSweep).toDouble())
                val radius = (this.size.width - strokeWidthPx) / 2f
                val dotCenter = Offset(
                    x = center.x + (radius * cos(endAngleRad)).toFloat(),
                    y = center.y + (radius * sin(endAngleRad)).toFloat()
                )

                // Halo glow behind the dot
                drawCircle(
                    color = (progressColors.lastOrNull() ?: BioLuminescentEmerald).copy(alpha = 0.40f),
                    radius = strokeWidthPx * 0.9f,
                    center = dotCenter
                )
                // Core bright dot
                drawCircle(
                    color = Color.White,
                    radius = strokeWidthPx * 0.45f,
                    center = dotCenter
                )
            }
        }

        // 4. Center Metric Content Slot
        if (centerContent != null) {
            centerContent()
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = primaryValue,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    if (!unitLabel.isNullOrBlank()) {
                        Text(
                            text = " $unitLabel",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = BioLuminescentEmerald,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }
                Text(
                    text = metricLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun ImpactMetricRingPreview() {
    Change360Theme(darkTheme = true) {
        Box(modifier = Modifier.padding(24.dp)) {
            ImpactMetricRing(
                progress = 0.72f,
                primaryValue = "18.4",
                unitLabel = "kg",
                metricLabel = "CO₂ Avoided"
            )
        }
    }
}
