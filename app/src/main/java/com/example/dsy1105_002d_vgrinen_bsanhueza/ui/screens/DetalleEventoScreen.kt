package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.AccionEvento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BannerAviso
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BarraSuperiorGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BotonPrincipal
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BotonSecundario
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.ChipPrioridad
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.EstadoGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TarjetaGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TextoSecundarioTarjeta
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.TituloSeccion
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.DetalleEventoViewModel

// Pantalla 4 del diseño: detalle del evento, acciones e historial.
// "usuario" es el correo de la sesión: queda registrado en cada acción.
@Composable
fun DetalleEventoScreen(
    viewModel: DetalleEventoViewModel,
    eventoId: String,
    usuario: String,
    puedeRegistrarAcciones: Boolean,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val nota by viewModel.nota.collectAsState()
    val enviando by viewModel.enviando.collectAsState()
    val errorAccion by viewModel.errorAccion.collectAsState()

    LaunchedEffect(eventoId) { viewModel.cargar(eventoId) }

    DetalleEventoContenido(
        estado = estado,
        nota = nota,
        enviando = enviando,
        errorAccion = errorAccion,
        puedeRegistrarAcciones = puedeRegistrarAcciones,
        onNotaChange = viewModel::onNotaChange,
        onTomar = { viewModel.tomarEvento(usuario) },
        onEscalar = { viewModel.escalar(usuario) },
        onMarcarAtendido = { viewModel.marcarAtendido(usuario) },
        onDescartarError = viewModel::descartarErrorAccion,
        onReintentar = viewModel::reintentar,
        onVolver = onVolver
    )
}

// Sin ViewModel: solo dibuja el estado que recibe (se puede probar con Compose UI Test)
@Composable
fun DetalleEventoContenido(
    estado: UiState<Evento>,
    nota: String,
    enviando: Boolean,
    errorAccion: String?,
    onNotaChange: (String) -> Unit,
    onTomar: () -> Unit,
    onEscalar: () -> Unit,
    onMarcarAtendido: () -> Unit,
    onDescartarError: () -> Unit,
    onReintentar: () -> Unit,
    onVolver: () -> Unit,
    // false para el rol de seguimiento (Supervisor): ve el evento y el historial, sin acciones
    puedeRegistrarAcciones: Boolean = true
) {
    val cargado = (estado as? UiState.Exito)?.datos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BarraSuperiorGuardian(
            titulo = cargado?.titulo ?: "Detalle del evento",
            subtitulo = cargado?.let { "Evento ${it.id} · ${it.hora}" },
            onVolver = onVolver
        )

        EstadoGuardian(estado, onReintentar, Modifier.weight(1f)) { evento, _ ->
            val atendido = evento.estado == EstadoAtencion.ATENDIDA

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Falló un PUT o un POST: se informa aquí y el usuario puede repetir la acción
                if (errorAccion != null) {
                    BannerAviso(errorAccion, onClick = onDescartarError, textoBoton = "Entendido")
                }

                TarjetaGuardian {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ChipPrioridad(evento.prioridad)
                        TextoSecundarioTarjeta("Estado: ${evento.estado.etiqueta}")
                    }
                    Text(
                        text = "${evento.camaraId} · ${evento.camaraNombre}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                    TextoSecundarioTarjeta("Ubicación referencial: ${evento.ubicacionReferencial}")
                }

                if (puedeRegistrarAcciones) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BotonPrincipal(
                            texto = "Tomar evento",
                            onClick = onTomar,
                            enabled = !enviando && evento.estado == EstadoAtencion.PENDIENTE,
                            modifier = Modifier.weight(1f)
                        )
                        BotonSecundario(
                            texto = "Escalar",
                            onClick = onEscalar,
                            enabled = !enviando && !atendido,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = nota,
                        onValueChange = onNotaChange,
                        label = { Text("Nota de atención") },
                        enabled = !atendido,
                        minLines = 2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    BotonPrincipal(
                        texto = "Marcar como atendido",
                        onClick = onMarcarAtendido,
                        enabled = !enviando && !atendido,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (enviando) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                } else {
                    // Rol de seguimiento: sin botones de acción
                    TarjetaGuardian {
                        TextoSecundarioTarjeta(
                            "Tu rol es de seguimiento: puedes revisar el evento y su historial. " +
                                "Las acciones las registra un Operador."
                        )
                    }
                }

                TituloSeccion("Historial de acciones")
                TarjetaGuardian {
                    // La acción más reciente va primero
                    evento.acciones.asReversed().forEach { accion ->
                        TextoSecundarioTarjeta(
                            texto = lineaHistorial(accion),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// "10:50 · operador@guardian.test · Tomó el evento" o "10:44 · Alerta generada por el sistema"
private fun lineaHistorial(accion: AccionEvento): String =
    if (accion.esDelSistema) "${accion.hora} · ${accion.descripcion}"
    else "${accion.hora} · ${accion.usuario} · ${accion.descripcion}"
