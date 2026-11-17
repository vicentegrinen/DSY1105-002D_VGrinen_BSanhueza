package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.PanelDatos
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.EventoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Panel operacional: indicadores del día y alertas más urgentes.
class PanelViewModel(
    private val repo: EventoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<PanelDatos>>(UiState.Cargando)
    val estado: StateFlow<UiState<PanelDatos>> = _estado

    // Último panel cargado con éxito: se muestra si una recarga posterior falla
    private var guardado: PanelDatos? = null

    // La pantalla lo llama al abrirse y desde el botón "Reintentar"
    fun cargar() {
        viewModelScope.launch {
            // Si ya hay datos en pantalla se mantienen mientras llega la respuesta
            if (_estado.value !is UiState.Exito) _estado.value = UiState.Cargando
            try {
                val indicadores = repo.obtenerIndicadores()
                val eventos = repo.obtenerEventos()
                val datos = PanelDatos(indicadores, alertasRecientes(eventos))
                guardado = datos
                _estado.value = UiState.Exito(datos)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // App recién abierta y sin red: se arma el panel con la copia guardada en el teléfono
                if (guardado == null) {
                    guardado = repo.indicadoresGuardados()?.let { indicadores ->
                        PanelDatos(indicadores, alertasRecientes(repo.eventosGuardados()))
                    }
                }
                _estado.value = UiState.Error(mensajeDeError(e, guardado != null), guardado)
            }
        }
    }

    // Eventos sin atender: primero los de mayor prioridad y, dentro de ella, los más nuevos
    private fun alertasRecientes(eventos: List<Evento>): List<Evento> =
        eventos
            .filter { it.estado != EstadoAtencion.ATENDIDA }
            .sortedWith(compareBy<Evento> { it.prioridad.ordinal }.thenByDescending { it.fechaHora })
            .take(MAX_ALERTAS)

    private companion object {
        const val MAX_ALERTAS = 3
    }
}

class PanelViewModelFactory(
    private val repo: EventoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        PanelViewModel(repo) as T
}
