package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.BordeChip

// Chip del diseño (esquinas de 10dp): azul cuando está seleccionado, blanco con borde si no.
// Se usa en los filtros de eventos y en la elección de rol.
@Composable
fun ChipSeleccion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val azul = MaterialTheme.colorScheme.primary
    Surface(
        selected = seleccionado,
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = if (seleccionado) azul else MaterialTheme.colorScheme.surface,
        contentColor = if (seleccionado) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, if (seleccionado) azul else BordeChip),
        // Deshabilitado: se ve atenuado para que se note que no responde
        modifier = modifier.alpha(if (enabled) 1f else 0.5f)
    ) {
        Text(
            text = texto,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
