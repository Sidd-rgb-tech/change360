package org.change360.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * CHANGE360 Material 3 Corner Shape System.
 *
 * Combines organic curvature with sleek glassmorphism geometry to match
 * circular planetary aesthetics and rounded eco-pills.
 */
val Change360Shapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Specialized Domain & Container Shapes
val GlassCardShape = RoundedCornerShape(20.dp)
val GlassCardSmallShape = RoundedCornerShape(14.dp)
val MetricPillShape = RoundedCornerShape(percent = 50)
val BottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
val AvatarRingShape = CircleShape

