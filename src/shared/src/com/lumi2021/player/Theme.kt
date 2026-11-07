package com.lumi2021.player

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AppColorScheme = darkColorScheme(
    primary = Color(0xFF9B72CF),
    onPrimary = Color(0xFFFFFFFF),

    secondary = Color(0xFF6A35B5),
    onSecondary = Color(0xFFFFFFFF),

    tertiary = Color(0xFF80CBC4),
    onTertiary = Color(0xFF003731),

    background = Color(0xFF111318),
    onBackground = Color(0xFFE4E1E9),

    surface = Color(0xFF1A1B22),
    onSurface = Color(0xFFE4E1E9),

    surfaceVariant = Color(0xFF30313A),
    onSurfaceVariant = Color(0xFFC5C5D0),

    outline = Color(0xFF8F8A99),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val AppTypography = Typography(
    titleLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
    headlineLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(fontSize = 16.sp)
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
