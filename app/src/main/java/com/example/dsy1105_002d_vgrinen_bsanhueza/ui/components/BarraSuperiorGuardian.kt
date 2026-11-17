package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.AzulGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.TextoBarraSecundario

// Barra superior azul del diseño: título, subtítulo opcional y flecha "Volver" opcional.
@Composable
fun BarraSuperiorGuardian(
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    onVolver: (() -> Unit)? = null
) {
    Surface(
        color = AzulGuardian,
        contentColor = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = if (onVolver != null) 8.dp else 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 14.dp
            )
        ) {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
            Column {
                Text(
                    text = titulo,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics { heading() }
                )
                if (subtitulo != null) {
                    Text(
                        text = subtitulo,
                        fontSize = 13.sp,
                        color = TextoBarraSecundario,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
