package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ThemeMode(val key: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromKey(key: String?): ThemeMode =
            entries.firstOrNull { it.key == key } ?: SYSTEM
    }
}

private val OwlLightColorScheme = lightColorScheme(
    primary = OwlTealPrimary,
    onPrimary = Color.White,
    primaryContainer = OwlMintContainer,
    onPrimaryContainer = OwlOnMintContainer,
    secondary = OwlAmber,
    onSecondary = Color.White,
    secondaryContainer = OwlAmberContainer,
    onSecondaryContainer = OwlOnAmberContainer,
    tertiary = OwlEmerald,
    onTertiary = Color.White,
    tertiaryContainer = OwlEmeraldContainer,
    onTertiaryContainer = OwlOnEmeraldContainer,
    error = OwlCoral,
    onError = Color.White,
    errorContainer = OwlCoralContainer,
    onErrorContainer = OwlOnCoralContainer,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = LightSurfaceElevated,
    surfaceContainer = LightSurfaceVariant,
    outline = LightOutline,
)

private val OwlDarkColorScheme = darkColorScheme(
    primary = OwlTealLightPrimary,
    onPrimary = OwlOnMintContainer,
    primaryContainer = OwlTealDarkContainer,
    onPrimaryContainer = OwlMintSoftText,
    secondary = OwlAmber,
    onSecondary = OwlOnAmberContainer,
    secondaryContainer = OwlAmberDarkContainer,
    onSecondaryContainer = OwlAmberContainer,
    tertiary = OwlEmerald,
    onTertiary = OwlOnEmeraldContainer,
    tertiaryContainer = OwlEmeraldDarkContainer,
    onTertiaryContainer = OwlEmeraldContainer,
    error = Color(0xFFFB7185),
    onError = Color(0xFF4C0519),
    errorContainer = OwlCoralDarkContainer,
    onErrorContainer = OwlCoralContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = Color(0xFF071010),
    surfaceContainerLow = DarkSurfaceElevated,
    surfaceContainer = DarkSurfaceVariant,
    outline = DarkOutline,
)

val OwlShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun ProjectOwlTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    isPersian: Boolean = true,
    content: @Composable () -> Unit,
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    MaterialTheme(
        colorScheme = if (useDarkTheme) OwlDarkColorScheme else OwlLightColorScheme,
        typography = owlTypography(isPersian = isPersian),
        shapes = OwlShapes,
        content = content,
    )
}
