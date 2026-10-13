package com.ytkf2.bahceasist.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ForestPrimary,
    onPrimary = OnForestPrimary,
    primaryContainer = ForestPrimaryContainer,
    onPrimaryContainer = OnForestPrimaryContainer,
    secondary = GardenSecondary,
    onSecondary = OnGardenSecondary,
    secondaryContainer = GardenSecondaryContainer,
    onSecondaryContainer = OnGardenSecondaryContainer,
    tertiary = WeatherTertiary,
    onTertiary = OnWeatherTertiary,
    tertiaryContainer = WeatherTertiaryContainer,
    onTertiaryContainer = OnWeatherTertiaryContainer,
    error = AlertError,
    onError = OnAlertError,
    errorContainer = AlertErrorContainer,
    onErrorContainer = OnAlertErrorContainer,
    background = GardenSurface,
    onBackground = OnGardenSurface,
    surface = GardenSurface,
    onSurface = OnGardenSurface,
    surfaceVariant = GardenSurfaceContainerHighest,
    onSurfaceVariant = OnGardenSurfaceVariant,
    outline = GardenOutline,
    outlineVariant = GardenOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = OnForestPrimaryContainer,
    onPrimary = ForestPrimary,
    primaryContainer = ForestPrimaryContainer,
    onPrimaryContainer = OnForestPrimaryContainer,
    secondary = GardenSecondaryFixed,
    onSecondary = ForestPrimary,
    secondaryContainer = GardenSecondary,
    onSecondaryContainer = OnGardenSecondary,
    tertiary = WeatherTertiaryFixed,
    onTertiary = WeatherTertiary,
    tertiaryContainer = WeatherTertiaryContainer,
    onTertiaryContainer = OnWeatherTertiaryContainer,
    error = AlertErrorContainer,
    onError = AlertError,
    background = OnGardenSurface,
    onBackground = GardenSurface,
    surface = OnGardenSurface,
    onSurface = GardenSurface
)

@Composable
fun BahceAsistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
                @Suppress("DEPRECATION")
                it.statusBarColor = colorScheme.surface.toArgb()
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
