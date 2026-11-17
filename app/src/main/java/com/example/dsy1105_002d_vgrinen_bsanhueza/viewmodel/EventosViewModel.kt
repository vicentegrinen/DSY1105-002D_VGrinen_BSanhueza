package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.FiltroEventos
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.EventoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Lista de eventos y alertas con chips de filtro.
class EventosViewModel(
    private val repo: EventoRepository
) : ViewModel() {

    // Lista ya filtrada que dibuja la pantalla
    private val _estado = MutableStateFlow<UiState<List<Evento>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Evento>>> = _estado

    private val _filtro = MutableStateFlow(FiltroEventos.TODOS)
    val filtro: StateFlow<FiltroEventos> = _filtro

    // Última lista completa recibida de la API (sin filtrar) y error de la última carga
    private var todos: List<Evento>? = null
    private var error: String? = null

    // La pantalla lo llama al abrirse y desde el botón "Reintentar"
    fun cargar() {
        viewModelScope.launch {
            if (_estado.value !is UiState.Exito) _estado.value = UiState.Cargando
            try {
                todos = repo.obtenerEventos()
                error = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // App recién abierta y sin red: se usa la copia guardada en el teléfono
                if (todos == null) {
                    todos = repo.eventosGuardados().takeIf { it.isNotEmpty() }
                }
                error = mensajeDeError(e, hayDatosGuardados = todos != null)
            }
            publicar()
        }
    }

    // El filtro se aplica sobre la lista ya cargada: no vuelve a llamar a la API
    fun cambiarFiltro(nuevo: FiltroEventos) {
        _filtro.value = nuevo
        if (_estado.value !is UiState.Cargando) publicar()
    }

    private fun publicar() {
        val filtrados = todos?.let { _filtro.value.aplicar(it) }
        val mensaje = error
        _estado.value = when {
            mensaje != null -> UiState.Error(mensaje, filtrados)
            filtrados != null -> UiState.Exito(filtrados)
            else -> UiState.Cargando
        }
    }
}

class EventosViewModelFactory(
    private val repo: EventoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        EventosViewModel(repo) as T
}
