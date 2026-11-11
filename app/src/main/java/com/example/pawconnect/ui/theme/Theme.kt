
package com.example.pawconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * PawConnect Light Colour Scheme
 */
private val PawConnectLightColorScheme = lightColorScheme(

    // Primary
    primary = PawConnectColors.PrimaryBlue,
    onPrimary = PawConnectColors.White,
    primaryContainer = PawConnectColors.SoftBlue,
    onPrimaryContainer = PawConnectColors.HeaderBlue,

    // Secondary
    secondary = PawConnectColors.HeaderBlue,
    onSecondary = PawConnectColors.White,
    secondaryContainer = PawConnectColors.ChipBlue,
    onSecondaryContainer = PawConnectColors.TextPrimary,

    // Tertiary / Accent
    tertiary = PawConnectColors.Coral,
    onTertiary = PawConnectColors.White,
    tertiaryContainer = PawConnectColors.SoftCoral,
    onTertiaryContainer = PawConnectColors.TextPrimary,

    // Background
    background = PawConnectColors.Background,
    onBackground = PawConnectColors.TextPrimary,

    // Surface
    surface = PawConnectColors.Surface,
    onSurface = PawConnectColors.TextPrimary,
    surfaceVariant = PawConnectColors.LightBlue,
    onSurfaceVariant = PawConnectColors.TextSecondary,

    // Borders
    outline = PawConnectColors.Border,

    // Error
    error = PawConnectColors.Error,
    onError = PawConnectColors.White,
    errorContainer = PawConnectColors.ErrorBackground,
    onErrorContainer = PawConnectColors.Error
)

/**
 * PawConnect Dark Colour Scheme
 */
private val PawConnectDarkColorScheme = darkColorScheme(

    // Primary
    primary = androidx.compose.ui.graphics.Color(0xFF9BCDF0),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF12314A),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF254B68),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFDCEEFF),

    // Secondary
    secondary = androidx.compose.ui.graphics.Color(0xFFB7D0E3),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF193548),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF304B60),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFDCEAF5),

    // Tertiary
    tertiary = androidx.compose.ui.graphics.Color(0xFFFFB5A9),
    onTertiary = androidx.compose.ui.graphics.Color(0xFF4A1F1B),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFF713B34),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFFDAD4),

    // Background
    background = androidx.compose.ui.graphics.Color(0xFF101A23),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE4EDF5),

    // Surface
    surface = androidx.compose.ui.graphics.Color(0xFF16232E),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE4EDF5),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF293B49),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFBDCAD5),

    // Borders
    outline = androidx.compose.ui.graphics.Color(0xFF7D91A1),

    // Error
    error = androidx.compose.ui.graphics.Color(0xFFFFB4AB),
    onError = androidx.compose.ui.graphics.Color(0xFF690005)
)

/**
 * Main PawConnect Theme
 *
 * Apply this theme to the entire application.
 */
@Composable
fun PawConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        PawConnectDarkColorScheme
    } else {
        PawConnectLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PawConnectTypography,
        content = content
    )
}