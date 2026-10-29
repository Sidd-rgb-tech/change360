package org.change360.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * CHANGE360 Bio-Luminescent Brand Color Tokens.
 *
 * Designed around an evocative Deep Forest Obsidian foundation infused with
 * vibrant planetary aura highlights: Bio-Luminescent Emerald, Solar Cyan, and Warm Amber.
 */

// Core Brand Identity Colors
val DeepForestObsidian = Color(0xFF081410)
val BioLuminescentEmerald = Color(0xFF00E676)
val SolarCyan = Color(0xFF00E5FF)
val WarmAmber = Color(0xFFFFD740)

// Planetary Accent Palettes
val MintGlow = Color(0xFF69F0AE)
val AquaTeal = Color(0xFF00BFA5)
val EmeraldDeep = Color(0xFF004D25)
val EmeraldContainer = Color(0xFF005B2D)
val SolarCyanDeep = Color(0xFF004E57)
val SolarCyanContainer = Color(0xFF005F6B)
val AmberDeep = Color(0xFF5E3F00)
val AmberContainer = Color(0xFF754F00)
val CoralAlert = Color(0xFFFF5252)
val CoralContainer = Color(0xFF93000A)

// Neutral & Surface Gradients (Dark Forest Spectrum)
val ObsidianBlack = Color(0xFF040A08)
val SurfaceDark = Color(0xFF0B1914)
val SurfaceContainerLowestDark = Color(0xFF050C0A)
val SurfaceContainerLowDark = Color(0xFF081410)
val SurfaceContainerDark = Color(0xFF0F221B)
val SurfaceContainerHighDark = Color(0xFF142C23)
val SurfaceContainerHighestDark = Color(0xFF1B382D)
val SurfaceVariantDark = Color(0xFF162A22)
val OnSurfaceVariantDark = Color(0xFFA3B8AE)
val OutlineDark = Color(0xFF284437)
val OutlineVariantDark = Color(0xFF183026)
val TextPrimaryDark = Color(0xFFE2EBE5)
val TextSecondaryDark = Color(0xFFA3B8AE)
val TextTertiaryDark = Color(0xFF72857C)

// Glassmorphic Surface Overlay Tokens
val GlassmorphicSurface = Color(0x1F142C23)
val GlassmorphicSurfaceHover = Color(0x331B382D)
val GlassmorphicBorder = Color(0x3300E676)
val GlassmorphicBorderSubtle = Color(0x1F00E5FF)
val GlassmorphicGlow = Color(0x2600E676)

// Light Theme Neutrals (Botanical Daylight Spectrum)
val BotanicalWhite = Color(0xFFF7FBF8)
val SurfaceLight = Color(0xFFF0F6F2)
val SurfaceContainerLight = Color(0xFFE5EEE8)
val SurfaceContainerHighLight = Color(0xFFDBE6DE)
val TextPrimaryLight = Color(0xFF0A1C15)
val TextSecondaryLight = Color(0xFF3F5349)
val OutlineLight = Color(0xFFB5C8BD)

/**
 * CHANGE360 Default Dark Color Scheme (Material 3).
 *
 * Implements the signature high-contrast Bio-Luminescent theme on Deep Forest Obsidian.
 */
val Change360DarkColorScheme = darkColorScheme(
    primary = BioLuminescentEmerald,
    onPrimary = Color(0xFF003918),
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = MintGlow,
    inversePrimary = Color(0xFF006D35),

    secondary = SolarCyan,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = SolarCyanContainer,
    onSecondaryContainer = Color(0xFFB2F5FF),

    tertiary = WarmAmber,
    onTertiary = Color(0xFF422C00),
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = Color(0xFFFFE082),

    background = DeepForestObsidian,
    onBackground = TextPrimaryDark,

    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,

    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,

    error = CoralAlert,
    onError = Color(0xFF410002),
    errorContainer = CoralContainer,
    onErrorContainer = Color(0xFFFFDAD6),

    inverseSurface = TextPrimaryDark,
    inverseOnSurface = DeepForestObsidian,
    scrim = Color(0xFF000000)
)

/**
 * CHANGE360 Light Color Scheme (Material 3).
 *
 * Daytime botanical counterpart balancing nature-inspired tones with crisp contrast.
 */
val Change360LightColorScheme = lightColorScheme(
    primary = Color(0xFF008945),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF86F8AC),
    onPrimaryContainer = Color(0xFF00210B),
    inversePrimary = MintGlow,

    secondary = Color(0xFF006874),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF97F0FF),
    onSecondaryContainer = Color(0xFF001F24),

    tertiary = Color(0xFF7A5900),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDF9E),
    onTertiaryContainer = Color(0xFF261900),

    background = BotanicalWhite,
    onBackground = TextPrimaryLight,

    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFDCE5DD),
    onSurfaceVariant = TextSecondaryLight,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF3F9F5),
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = Color(0xFFD2DED6),

    outline = OutlineLight,
    outlineVariant = Color(0xFFCBD8D0),

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    inverseSurface = Color(0xFF2E3531),
    inverseOnSurface = BotanicalWhite,
    scrim = Color(0xFF000000)
)

