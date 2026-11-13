package com.example.agricultureoffline.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AgriColorScheme = darkColorScheme(
    primary = AgriColors.LeafBright,
    onPrimary = AgriColors.Cream,
    secondary = AgriColors.Mint,
    onSecondary = AgriColors.ForestDeep,
    tertiary = AgriColors.Success,
    background = AgriColors.ForestDeep,
    onBackground = AgriColors.TextPrimary,
    surface = AgriColors.Forest,
    onSurface = AgriColors.TextPrimary,
    surfaceVariant = AgriColors.ForestMid,
    onSurfaceVariant = AgriColors.TextSecondary,
    error = AgriColors.Danger,
    onError = AgriColors.ForestDeep
)

@Composable
fun AgricultureOfflineTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = true,
    @Suppress("UNUSED_PARAMETER") dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AgriColorScheme,
        typography = Typography,
        content = content
    )
}
