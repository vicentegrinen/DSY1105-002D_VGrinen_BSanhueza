package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme

import android.app.Activity
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

// Modo Guardián: versión oscura de la paleta del diseño
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA9C4F5),
    onPrimary = Color(0xFF0B2347),
    primaryContainer = AzulGuardian,
    onPrimaryContainer = SeleccionGuardian,
    secondary = Color(0xFFA9C4F5),
    onSecondary = Color(0xFF0B2347),
    secondaryContainer = Color(0xFF2B4674),
    onSecondaryContainer = SeleccionGuardian,
    tertiary = Color(0xFF9BD4A0),
    background = Color(0xFF0F1524),
    onBackground = Color(0xFFE3E8F2),
    surface = Color(0xFF182033),
    onSurface = Color(0xFFE3E8F2),
    surfaceVariant = Color(0xFF232D45),
    onSurfaceVariant = Color(0xFFB4BED3),
    surfaceContainerLowest = Color(0xFF0F1524),
    surfaceContainerLow = Color(0xFF182033),
    surfaceContainer = Color(0xFF182033),
    surfaceContainerHigh = Color(0xFF232D45),
    surfaceContainerHighest = Color(0xFF2E3A52),
    outline = BordeCampo,
    outlineVariant = Color(0xFF2E3A52),
    error = Color(0xFFFFB4AB)
)

// Modo Guardián: paleta del diseño (barra #1B3A6B, fondo #F4F6FA, tarjetas blancas)
private val LightColorScheme = lightColorScheme(
    primary = AzulGuardian,
    onPrimary = Color.White,
    primaryContainer = SeleccionGuardian,
    onPrimaryContainer = TextoPrincipal,
    secondary = AzulGuardian,
    onSecondary = Color.White,
    secondaryContainer = SeleccionGuardian,
    onSecondaryContainer = TextoPrincipal,
    tertiary = BajaTexto,
    background = FondoGuardian,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = FondoGuardian,
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color(0xFFEEF1F7),
    surfaceContainerHighest = Color(0xFFE6EAF2),
    outline = BordeCampo,
    outlineVariant = BordeTarjeta,
    error = CriticaTexto,
    onError = Color.White,
    errorContainer = CriticaFondo,
    onErrorContainer = CriticaTexto
)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */


@Composable
fun DSY1105002D_VGrinen_BSanhuezaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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