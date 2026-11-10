package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MkGoldLight,
    onPrimary = MkDarkGreen,
    primaryContainer = MkEmerald,
    onPrimaryContainer = MkGoldLight,
    secondary = MkGold,
    onSecondary = MkDarkGreen,
    secondaryContainer = MkDarkSurfaceVariant,
    onSecondaryContainer = MkGoldLight,
    tertiary = MkGold,
    onTertiary = MkDarkGreen,
    background = MkDarkBg,
    onBackground = Color(0xFFE4EDE7),
    surface = MkDarkSurface,
    onSurface = Color(0xFFE4EDE7),
    surfaceVariant = MkDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB5C5BD),
    error = MkError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = MkDarkGreen,
    onPrimary = Color.White,
    primaryContainer = MkEmerald,
    onPrimaryContainer = Color.White,
    secondary = MkGold,
    onSecondary = MkDarkGreen,
    secondaryContainer = MkGoldLight.copy(alpha = 0.25f),
    onSecondaryContainer = MkDarkGreen,
    tertiary = MkGoldDark,
    onTertiary = Color.White,
    background = MkLightBg,
    onBackground = MkTextPrimary,
    surface = MkSurface,
    onSurface = MkTextPrimary,
    surfaceVariant = MkSurfaceVariant,
    onSurfaceVariant = MkTextSecondary,
    error = MkError,
    onError = Color.White
)

@Composable
fun MkShopsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep brand emerald & gold palette consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
