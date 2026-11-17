package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Camara
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BannerAviso
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BarraSuperiorGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.EstadoGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.EtiquetaColor
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TarjetaGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TextoSecundarioTarjeta
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.BajaFondo
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.BajaTexto
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.CriticaFondo
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.CriticaTexto
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.CamarasViewModel

// Pantalla 5 del diseño: cámaras simuladas y aviso "Sin conexión" con Reintentar.
@Composable
fun CamarasScreen(viewModel: CamarasViewModel) {
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(Unit) { viewModel.cargar() }

    CamarasContenido(estado = estado, onReintentar = viewModel::cargar)
}

// Sin ViewModel: solo dibuja el estado que recibe (se puede probar con Compose UI Test)
@Composable
fun CamarasContenido(
    estado: UiState<List<Camara>>,
    onReintentar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BarraSuperiorGuardian(titulo = "Cámaras", subtitulo = "Dispositivos simulados")

        EstadoGuardian(estado, onReintentar, Modifier.weight(1f)) { camaras, mensajeError ->
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (mensajeError != null) {
                    item { BannerAviso(mensajeError, onClick = onReintentar) }
                }
                items(camaras, key = { it.id }) { camara -> TarjetaCamara(camara) }
            }
        }
    }
}

@Composable
private fun TarjetaCamara(camara: Camara) {
    TarjetaGuardian {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${camara.id} · ${camara.nombre}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            // Estado con color y texto
            if (camara.enLinea) {
                EtiquetaColor("En línea", BajaFondo, BajaTexto)
            } else {
                EtiquetaColor("Sin señal", CriticaFondo, CriticaTexto)
            }
        }
        TextoSecundarioTarjeta(detalleDe(camara), modifier = Modifier.padding(top = 2.dp))
    }
}

private fun detalleDe(camara: Camara): String = when {
    !camara.enLinea -> "Última señal: ${camara.horaUltimaSenal ?: "sin registro"}"
    camara.alertasPendientes == 1 -> "1 alerta pendiente"
    camara.alertasPendientes > 1 -> "${camara.alertasPendientes} alertas pendientes"
    else -> "Monitoreo activo"
}
