package com.example.frontend

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography

val DarkBlue = Color(0xFF001F3F)   // Custom Primary Color
val LightBlue = Color(0xFF007BFF)

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,  // Use your custom primary color
    secondary = LightBlue
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkBlue,
    secondary = LightBlue
)

@Composable
fun MyAppTheme(
    darkTheme: Boolean = false, // Change to `isSystemInDarkTheme()` for auto mode
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        //typography = Typography, // Default typography
        content = content
    )
}