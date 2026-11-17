package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Indicadores
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.PanelDatos
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BannerAviso
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BarraSuperiorGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.EstadoGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TarjetaEvento
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TarjetaGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TextoSecundarioTarjeta
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TituloSeccion
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.PanelViewModel

// Pantalla 2 del diseño: panel operacional (reemplaza a BienvenidaScreen tras el login).
@Composable
fun PanelScreen(
    viewModel: PanelViewModel,
    correo: String,
    rol: Rol?,
    onAbrirEvento: (String) -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    // Se recarga cada vez que se entra a la pestaña (por ejemplo, al volver de un detalle)
    LaunchedEffect(Unit) { viewModel.cargar() }

    PanelContenido(
        estado = estado,
        subtitulo = listOfNotNull(correo.ifBlank { null }, rol?.let { "Rol: ${it.etiqueta}" })
            .joinToString(" · "),
        onReintentar = viewModel::cargar,
        onAbrirEvento = onAbrirEvento
    )
}

// Sin ViewModel: solo dibuja el estado que recibe (se puede probar con Compose UI Test)
@Composable
fun PanelContenido(
    estado: UiState<PanelDatos>,
    subtitulo: String,
    onReintentar: () -> Unit,
    onAbrirEvento: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BarraSuperiorGuardian(titulo = "Panel operacional", subtitulo = subtitulo.ifBlank { null })

        EstadoGuardian(estado, onReintentar, Modifier.weight(1f)) { datos, mensajeError ->
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (mensajeError != null) {
                    item { BannerAviso(mensajeError, onClick = onReintentar) }
                }
                item { TarjetasIndicadores(datos.indicadores) }
                item { TituloSeccion("Alertas críticas recientes") }
                if (datos.alertasRecientes.isEmpty()) {
                    item { TextoSecundarioTarjeta("No hay alertas pendientes.") }
                }
                items(datos.alertasRecientes, key = { it.id }) { evento ->
                    TarjetaEvento(
                        evento = evento,
                        detalle = "${evento.camaraId} · ${evento.camaraNombre}",
                        onClick = { onAbrirEvento(evento.id) }
                    )
                }
            }
        }
    }
}

// Las 4 tarjetas de indicadores en una grilla de 2 x 2
@Composable
private fun TarjetasIndicadores(indicadores: Indicadores) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaIndicador(
                valor = indicadores.eventosHoy.toString(),
                etiqueta = "Eventos hoy",
                modifier = Modifier.weight(1f)
            )
            TarjetaIndicador(
                valor = indicadores.alertasPendientes.toString(),
                etiqueta = "Alertas pendientes",
                colorValor = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaIndicador(
                valor = indicadores.atendidas.toString(),
                etiqueta = "Atendidas",
                colorValor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
            TarjetaIndicador(
                valor = "${indicadores.minutosRespuestaPromedio} min",
                etiqueta = "Respuesta promedio",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TarjetaIndicador(
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier,
    colorValor: Color = MaterialTheme.colorScheme.onSurface
) {
    // mergeDescendants: el lector de pantalla anuncia "24, Eventos hoy" como un solo elemento
    TarjetaGuardian(modifier = modifier.semantics(mergeDescendants = true) {}) {
        Text(text = valor, fontSize = 28.sp, fontWeight = FontWeight.Medium, color = colorValor)
        TextoSecundarioTarjeta(etiqueta)
    }
}
