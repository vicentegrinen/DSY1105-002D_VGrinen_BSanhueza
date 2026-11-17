package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.FiltroEventos
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BannerAviso
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BarraSuperiorGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.ChipSeleccion
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.EstadoGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TarjetaEvento
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TextoSecundarioTarjeta
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.EventosViewModel

// Pantalla 3 del diseño: eventos y alertas con chips de filtro.
@Composable
fun EventosScreen(
    viewModel: EventosViewModel,
    onAbrirEvento: (String) -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val filtro by viewModel.filtro.collectAsState()

    // Se recarga al entrar: así refleja los cambios hechos en el detalle
    LaunchedEffect(Unit) { viewModel.cargar() }

    EventosContenido(
        estado = estado,
        filtro = filtro,
        onFiltro = viewModel::cambiarFiltro,
        onReintentar = viewModel::cargar,
        onAbrirEvento = onAbrirEvento
    )
}

// Sin ViewModel: solo dibuja el estado que recibe (se puede probar con Compose UI Test)
@Composable
fun EventosContenido(
    estado: UiState<List<Evento>>,
    filtro: FiltroEventos,
    onFiltro: (FiltroEventos) -> Unit,
    onReintentar: () -> Unit,
    onAbrirEvento: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BarraSuperiorGuardian(titulo = "Eventos y alertas", subtitulo = subtituloDe(estado))
        ChipsFiltro(seleccionado = filtro, onFiltro = onFiltro)

        EstadoGuardian(estado, onReintentar, Modifier.weight(1f)) { eventos, mensajeError ->
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (mensajeError != null) {
                    item { BannerAviso(mensajeError, onClick = onReintentar) }
                }
                if (eventos.isEmpty()) {
                    item { TextoSecundarioTarjeta("No hay eventos para este filtro.") }
                }
                items(eventos, key = { it.id }) { evento ->
                    TarjetaEvento(
                        evento = evento,
                        detalle = "${evento.camaraId} · ${evento.estado.etiqueta}",
                        onClick = { onAbrirEvento(evento.id) }
                    )
                }
            }
        }
    }
}

private fun subtituloDe(estado: UiState<List<Evento>>): String = when (estado) {
    is UiState.Cargando -> "Cargando…"
    is UiState.Exito -> if (estado.datos.size == 1) "1 evento" else "${estado.datos.size} eventos"
    is UiState.Error -> "Sin actualizar"
}

// Chips del diseño: Todos, Críticas, Pendientes, Atendidas (uno activo a la vez)
@Composable
private fun ChipsFiltro(
    seleccionado: FiltroEventos,
    onFiltro: (FiltroEventos) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        FiltroEventos.entries.forEach { filtro ->
            ChipSeleccion(
                texto = filtro.etiqueta,
                seleccionado = filtro == seleccionado,
                onClick = { onFiltro(filtro) }
            )
        }
    }
}
