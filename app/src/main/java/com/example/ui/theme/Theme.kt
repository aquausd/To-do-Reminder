package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AccentBlack,
    onPrimary = SurfaceWhite,
    secondary = TextSecondary,
    onSecondary = SurfaceWhite,
    background = BackgroundWhite,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SearchBackground,
    onSurfaceVariant = TextSecondary,
    outline = DividerLight,
    outlineVariant = BorderSubtle
)

@Composable
fun TodoReminderTheme(
    content: @Composable () -> Unit
) {
    // Explicit clean white/light monochrome theme
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
