package org.change360.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.GlassCardShape
import org.change360.app.ui.theme.SolarCyan

/**
 * Default subtle gradient border for CHANGE360 glassmorphic surfaces.
 */
val DefaultGlassmorphicBorderBrush = Brush.linearGradient(
    colors = listOf(
        BioLuminescentEmerald.copy(alpha = 0.40f),
        SolarCyan.copy(alpha = 0.20f),
        Color.White.copy(alpha = 0.08f),
        Color.Transparent
    )
)

/**
 * Default translucent background gradient brush mimicking frosted obsidian glass.
 */
val DefaultGlassmorphicSurfaceBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x33142C23), // subtle emerald-tinted surface
        Color(0x1F0F221B),
        Color(0x14081410)  // deep obsidian base
    )
)

/**
 * Translucent Glassmorphic Surface Container.
 *
 * Implements an ethereal frosted glass visual container featuring:
 * - Alpha-blended linear gradient surface background
 * - Subtle multi-stop border stroke with bio-luminescent emerald highlight
 * - Smooth corner clipping via [shape]
 * - Optional click interaction with tactile accessibility roles
 *
 * @param modifier Layout modifier applied to the outer container.
 * @param shape Corner clipping shape, defaulting to [GlassCardShape] (20.dp rounded corners).
 * @param surfaceBrush Gradient brush used for the translucent frosted glass interior.
 * @param borderStroke Border stroke applied to the card perimeter. Set null to omit border.
 * @param contentPadding Inner padding applied around the child content.
 * @param onClick Optional tap callback. When non-null, adds ripple and accessibility semantics.
 * @param content Composable column content hosted within the glass surface.
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = GlassCardShape,
    surfaceBrush: Brush = DefaultGlassmorphicSurfaceBrush,
    borderStroke: BorderStroke? = BorderStroke(width = 1.dp, brush = DefaultGlassmorphicBorderBrush),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            role = Role.Button,
            onClick = onClick
        )
    } else {
        Modifier
    }

    val borderModifier = if (borderStroke != null) {
        Modifier.border(
            border = borderStroke,
            shape = shape
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(clickableModifier)
            .background(brush = surfaceBrush, shape = shape)
            .then(borderModifier)
            .padding(contentPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}

/**
 * Lightweight Box overload of [GlassmorphicCard] allowing custom alignment and layout configurations.
 */
@Composable
fun GlassmorphicBox(
    modifier: Modifier = Modifier,
    shape: Shape = GlassCardShape,
    surfaceBrush: Brush = DefaultGlassmorphicSurfaceBrush,
    borderStroke: BorderStroke? = BorderStroke(width = 1.dp, brush = DefaultGlassmorphicBorderBrush),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    contentAlignment: Alignment = Alignment.TopStart,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            role = Role.Button,
            onClick = onClick
        )
    } else {
        Modifier
    }

    val borderModifier = if (borderStroke != null) {
        Modifier.border(
            border = borderStroke,
            shape = shape
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(clickableModifier)
            .background(brush = surfaceBrush, shape = shape)
            .then(borderModifier)
            .padding(contentPadding),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun GlassmorphicCardPreview() {
    Change360Theme(darkTheme = true) {
        Box(modifier = Modifier.padding(24.dp)) {
            GlassmorphicCard(
                onClick = {}
            ) {
                Text(
                    text = "Zero-Waste Lifestyle Quest",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Divert 5.2 kg of landfill packaging through reusable glass and bulk replenishment.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
