package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPinkPrimary,
    onPrimary = Color(0xFF4D0026),
    primaryContainer = DarkPinkPrimaryContainer,
    onPrimaryContainer = DarkOnPinkPrimaryContainer,
    secondary = DarkVioletSecondary,
    onSecondary = Color(0xFF38004E),
    secondaryContainer = DarkVioletSecondaryContainer,
    onSecondaryContainer = DarkOnVioletSecondaryContainer,
    tertiary = PinkTertiary,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFE2C4D0),
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = PinkPrimaryContainer,
    onPrimaryContainer = OnPinkPrimaryContainer,
    secondary = VioletSecondary,
    onSecondary = Color.White,
    secondaryContainer = VioletSecondaryContainer,
    onSecondaryContainer = OnVioletSecondaryContainer,
    tertiary = PinkTertiary,
    onTertiary = Color.White,
    tertiaryContainer = PinkTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF5D424E),
    outline = LightOutline
)

@Composable
fun PinkCatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
