package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Prioridad

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val NaranjaPrincipal = Color(0xFFE67E22)
val CremaFondo = Color(0xFFFFF8F0)
val VerdeAcento = Color(0xFF27AE60)

// ---- Modo Guardián: colores del diseño (modo-guardian-diseno.html) ----
val AzulGuardian = Color(0xFF1B3A6B)          // barra superior y botones
val FondoGuardian = Color(0xFFF4F6FA)         // fondo de las pantallas
val TextoPrincipal = Color(0xFF14213D)
val TextoSecundario = Color(0xFF46526B)
val TextoBarraSecundario = Color(0xFFC9D6EE)  // subtítulo sobre la barra azul
val BordeTarjeta = Color(0xFFDDE3EE)
val BordeCampo = Color(0xFF8896B0)
val BordeChip = Color(0xFFB8C3D9)
val SeleccionGuardian = Color(0xFFDCE6F7)     // pestaña activa de la barra inferior

// Prioridad: siempre se muestra con color Y texto (no depender solo del color)
val CriticaFondo = Color(0xFFFCE8E6)
val CriticaTexto = Color(0xFF9B1C14)
val MediaFondo = Color(0xFFFFF1D6)
val MediaTexto = Color(0xFF7A4E00)
val MediaBorde = Color(0xFFE8C77A)            // borde del aviso "Sin conexión"
val BajaFondo = Color(0xFFE3F4E5)
val BajaTexto = Color(0xFF1B5E20)

// Par fondo/texto de una etiqueta de color
data class ColoresEtiqueta(val fondo: Color, val texto: Color)

fun Prioridad.colores(): ColoresEtiqueta = when (this) {
    Prioridad.CRITICA -> ColoresEtiqueta(CriticaFondo, CriticaTexto)
    Prioridad.MEDIA -> ColoresEtiqueta(MediaFondo, MediaTexto)
    Prioridad.BAJA -> ColoresEtiqueta(BajaFondo, BajaTexto)
}