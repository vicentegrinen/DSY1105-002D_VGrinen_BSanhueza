package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Prioridad
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.colores

private val FormaTarjeta = RoundedCornerShape(16.dp)

// Tarjeta base del diseño: fondo de superficie, esquinas de 16dp y borde fino.
// Con onClick se vuelve presionable (por ejemplo, para abrir el detalle de un evento).
@Composable
fun TarjetaGuardian(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val colores = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    val borde = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = FormaTarjeta,
            colors = colores,
            border = borde
        ) {
            Column(modifier = Modifier.padding(14.dp), content = contenido)
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = FormaTarjeta,
            colors = colores,
            border = borde
        ) {
            Column(modifier = Modifier.padding(14.dp), content = contenido)
        }
    }
}

// Etiqueta redondeada de color con texto (prioridad, "En línea", "Sin señal")
@Composable
fun EtiquetaColor(
    texto: String,
    fondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = texto,
        color = colorTexto,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    )
}

// Prioridad con color y texto: crítica (rojo), media (ámbar), baja (verde)
@Composable
fun ChipPrioridad(prioridad: Prioridad, modifier: Modifier = Modifier) {
    val colores = prioridad.colores()
    EtiquetaColor(prioridad.etiqueta, colores.fondo, colores.texto, modifier)
}

// Texto secundario pequeño de las tarjetas
@Composable
fun TextoSecundarioTarjeta(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

// Título dentro de una tarjeta o de una sección ("Alertas críticas recientes")
@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.semantics { heading() }
    )
}

// Tarjeta de evento de las listas: prioridad, hora, título y una línea de detalle
@Composable
fun TarjetaEvento(
    evento: Evento,
    detalle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TarjetaGuardian(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChipPrioridad(evento.prioridad)
            TextoSecundarioTarjeta(evento.hora)
        }
        Text(
            text = evento.titulo,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
        )
        TextoSecundarioTarjeta(detalle)
    }
}
