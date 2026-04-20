package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MochaLavender,
    secondary = MochaMauve,
    tertiary = MochaGreen,
    background = MochaBase,
    surface = MochaSurface,
    onPrimary = MochaBase,
    onSurface = MochaText,
    error = MochaRed,
    onSecondary = MochaBase
)

private val LightColorScheme = lightColorScheme(
    primary = LatteLavender,
    secondary = LatteMauve,
    tertiary = LatteGreen,
    background = LatteBase,
    surface = LatteSurface,
    onPrimary = LatteBase,
    onSurface = LatteText,
    error = LatteRed,
    onSecondary = Color.White
)

@Composable
fun SmartBowlTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}