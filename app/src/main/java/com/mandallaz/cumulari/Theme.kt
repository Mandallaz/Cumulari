package com.mandallaz.cumulari

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Palette de secours pour les appareils antérieurs à Android 12 (pas de Material You)
private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF2196F3),
    secondary = androidx.compose.ui.graphics.Color(0xFF4CAF50)
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF90CAF9),
    secondary = androidx.compose.ui.graphics.Color(0xFFA5D6A7)
)

@Composable
fun CumulariTheme(
    // Suit automatiquement le réglage clair/sombre du téléphone
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Material You (couleurs extraites du fond d'écran) sur Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}