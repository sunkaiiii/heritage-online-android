package com.duckylife.heritage.modern.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duckylife.heritage.modern.core.settings.AppThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF8F372F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD4),
    onPrimaryContainer = Color(0xFF3A0905),
    secondary = Color(0xFF6B5852),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFE2DC),
    onSecondaryContainer = Color(0xFF261915),
    tertiary = Color(0xFF735C23),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE1A6),
    onTertiaryContainer = Color(0xFF261A00),
    background = Color(0xFFFCF8F5),
    onBackground = Color(0xFF211A18),
    surface = Color(0xFFFCF8F5),
    onSurface = Color(0xFF211A18),
    surfaceVariant = Color(0xFFEADDD7),
    onSurfaceVariant = Color(0xFF51443F),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFBF3EF),
    surfaceContainer = Color(0xFFF5ECE7),
    surfaceContainerHigh = Color(0xFFEFE3DE),
    surfaceContainerHighest = Color(0xFFE8DAD4),
    outline = Color(0xFF83736D),
    outlineVariant = Color(0xFFD6C2BA),
    inverseSurface = Color(0xFF372E2B),
    inverseOnSurface = Color(0xFFFFEDE8),
    inversePrimary = Color(0xFFFFB4AA),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB4AA),
    onPrimary = Color(0xFF561E19),
    primaryContainer = Color(0xFF733028),
    onPrimaryContainer = Color(0xFFFFDAD4),
    secondary = Color(0xFFD8C2BA),
    onSecondary = Color(0xFF3B2A25),
    secondaryContainer = Color(0xFF51403A),
    onSecondaryContainer = Color(0xFFF5DED6),
    tertiary = Color(0xFFE2C47C),
    onTertiary = Color(0xFF3F2E00),
    tertiaryContainer = Color(0xFF594419),
    onTertiaryContainer = Color(0xFFFFE1A6),
    background = Color(0xFF16100E),
    onBackground = Color(0xFFEDE0DC),
    surface = Color(0xFF16100E),
    onSurface = Color(0xFFEDE0DC),
    surfaceVariant = Color(0xFF51443F),
    onSurfaceVariant = Color(0xFFD6C2BA),
    surfaceContainerLowest = Color(0xFF100B09),
    surfaceContainerLow = Color(0xFF241D1A),
    surfaceContainer = Color(0xFF2A211E),
    surfaceContainerHigh = Color(0xFF362B27),
    surfaceContainerHighest = Color(0xFF433631),
    outline = Color(0xFF9F8D86),
    outlineVariant = Color(0xFF5D4C45),
    inverseSurface = Color(0xFFEDE0DC),
    inverseOnSurface = Color(0xFF372E2B),
    inversePrimary = Color(0xFF8F372F),
)

private val HeritageTypography = Typography(
    displaySmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 42.sp,
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 27.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
)

private val HeritageShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
)

@Composable
fun HeritageTheme(
    themeMode: AppThemeMode = AppThemeMode.System,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.System -> isSystemInDarkTheme()
        AppThemeMode.Light -> false
        AppThemeMode.Dark -> true
    }

    val colorScheme = when {
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HeritageTypography,
        shapes = HeritageShapes,
        content = content,
    )
}
