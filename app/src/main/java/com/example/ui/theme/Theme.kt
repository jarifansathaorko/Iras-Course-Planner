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

private val StitchDarkColorScheme = darkColorScheme(
    primary = StitchPrimary,
    onPrimary = StitchOnPrimary,
    primaryContainer = StitchPrimaryContainer,
    onPrimaryContainer = StitchOnPrimaryContainer,
    secondary = StitchSecondary,
    onSecondary = StitchOnSecondary,
    secondaryContainer = StitchSecondaryContainer,
    onSecondaryContainer = Color(0xFFB3B1FF),
    tertiary = StitchTertiary,
    onTertiary = StitchOnTertiary,
    tertiaryContainer = StitchTertiaryContainer,
    onTertiaryContainer = Color(0xFF00311F),
    background = StitchBackground,
    onBackground = StitchTextPrimary,
    surface = StitchSurface,
    onSurface = StitchTextPrimary,
    surfaceVariant = StitchSurfaceContainer,
    onSurfaceVariant = StitchTextSecondary,
    outline = StitchOutline,
    outlineVariant = StitchOutlineVariant,
    error = ConflictRed,
    errorContainer = ConflictRedBorder,
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val StitchLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    tertiary = TertiaryGold,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFDC2626)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to Stitch sleek dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> StitchDarkColorScheme
        else -> StitchLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
