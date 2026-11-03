package ru.offerfactory.alfasobes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RegistrationColors = lightColorScheme(
    primary = Color(0xFFE10A17),
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onSurface = Color(0xFF111111),
    onSurfaceVariant = Color(0xFF555555),
    outline = Color(0xFF9CA0A8),
    error = Color(0xFFE00000),
)

@Composable
fun AlfasobesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RegistrationColors,
        typography = Typography,
        content = content,
    )
}
