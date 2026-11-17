package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Botones del diseño: 48dp de alto mínimo y esquinas de 24dp.
private val FormaBoton = RoundedCornerShape(24.dp)
private val AltoMinimo = 48.dp

// Botón relleno (acción principal)
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = FormaBoton,
        modifier = modifier.heightIn(min = AltoMinimo)
    ) {
        Text(texto, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

// Botón con borde (acción secundaria, por ejemplo "Escalar")
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colorBorde =
        if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = FormaBoton,
        border = BorderStroke(1.dp, colorBorde),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.heightIn(min = AltoMinimo)
    ) {
        Text(texto, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
