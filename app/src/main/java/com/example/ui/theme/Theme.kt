package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SophisticatedDarkColorScheme = darkColorScheme(
    primary = SophisticatedPrimary,
    onPrimary = SophisticatedOnPrimary,
    primaryContainer = SophisticatedActiveTagBg,
    onPrimaryContainer = SophisticatedPrimary,
    secondary = SophisticatedSecondary,
    onSecondary = SophisticatedTextPrimary,
    secondaryContainer = SophisticatedSecondaryContainer,
    onSecondaryContainer = SophisticatedPrimary,
    background = SophisticatedBg,
    onBackground = SophisticatedTextPrimary,
    surface = SophisticatedSurface,
    onSurface = SophisticatedTextPrimary,
    surfaceVariant = SophisticatedNavBg,
    onSurfaceVariant = SophisticatedTextSecondary,
    outline = SophisticatedBorder,
    outlineVariant = SophisticatedBorderAlternative,
    error = SophisticatedError,
    onError = SophisticatedOnPrimary,
    errorContainer = SophisticatedErrorContainer,
    onErrorContainer = SophisticatedError
)

// In "Sophisticated Dark", both light and dark settings adopt this luxurious, eye-friendly deep dark canvas
private val SophisticatedLightColorScheme = SophisticatedDarkColorScheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We default dynamicColor to false to maintain our designed brand Sophisticated Dark aesthetic on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        SophisticatedDarkColorScheme
    } else {
        // Even in light themes, "Sophisticated Dark" operates in dark mode to preserve its signature sophisticated mood
        SophisticatedDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
