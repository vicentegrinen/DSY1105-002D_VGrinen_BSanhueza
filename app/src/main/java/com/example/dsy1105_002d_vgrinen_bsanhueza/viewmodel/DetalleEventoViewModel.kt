package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.EventoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Detalle de un evento: datos, nota de atención, acciones e historial.
// Cada acción deja registro de quién la hizo; "usuario" es el correo de la sesión actual.
class DetalleEventoViewModel(
    private val repo: EventoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<Evento>>(UiState.Cargando)
    val estado: StateFlow<UiState<Evento>> = _estado

    // Texto del campo "Nota de atención"
    private val _nota = MutableStateFlow("")
    val nota: StateFlow<String> = _nota

    // true mientras se envía una acción: la pantalla deshabilita los botones
    private val _enviando = MutableStateFlow(false)
    val enviando: StateFlow<Boolean> = _enviando

    // Error de la última acción (PUT o POST); null si no hubo
    private val _errorAccion = MutableStateFlow<String?>(null)
    val errorAccion: StateFlow<String?> = _errorAccion

    private var eventoId: String? = null

    fun cargar(id: String) {
        // Si ese evento ya está en pantalla (por ejemplo, al rotar) no se vuelve a pedir
        if (id == eventoId && _estado.value is UiState.Exito) return
        eventoId = id
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            try {
                _estado.value = UiState.Exito(repo.obtenerEvento(id))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Error(mensajeDeError(e))
            }
        }
    }

    // Botón "Reintentar" cuando falló la carga del detalle
    fun reintentar() {
        eventoId?.let { cargar(it) }
    }

    fun onNotaChange(valor: String) {
        _nota.value = valor
    }

    fun descartarErrorAccion() {
        _errorAccion.value = null
    }

    // "Tomar evento": solo si está pendiente. Pasa a "En atención".
    fun tomarEvento(usuario: String) {
        val evento = eventoActual() ?: return
        if (evento.estado != EstadoAtencion.PENDIENTE) return
        ejecutar(evento, usuario, EstadoAtencion.EN_ATENCION, "Tomó el evento")
    }

    // "Escalar": no cambia el estado, solo deja constancia en el historial
    fun escalar(usuario: String) {
        val evento = eventoActual() ?: return
        if (evento.estado == EstadoAtencion.ATENDIDA) return
        ejecutar(evento, usuario, nuevoEstado = null, descripcion = "Escaló el evento")
    }

    // "Marcar como atendido": cierra el evento y guarda la nota (si se escribió) en el historial
    fun marcarAtendido(usuario: String) {
        val evento = eventoActual() ?: return
        if (evento.estado == EstadoAtencion.ATENDIDA) return
        val nota = _nota.value.trim()
        val descripcion =
            if (nota.isEmpty()) "Marcó como atendido" else "Marcó como atendido: $nota"
        ejecutar(evento, usuario, EstadoAtencion.ATENDIDA, descripcion, limpiarNota = true)
    }

    private fun eventoActual(): Evento? = (_estado.value as? UiState.Exito)?.datos

    // Primero cambia el estado (PUT) y después registra la acción (POST).
    // La pantalla se actualiza tras cada paso, así refleja lo que el servidor ya guardó
    // aunque el segundo paso falle.
    private fun ejecutar(
        evento: Evento,
        usuario: String,
        nuevoEstado: EstadoAtencion?,
        descripcion: String,
        limpiarNota: Boolean = false
    ) {
        if (_enviando.value) return
        _enviando.value = true
        _errorAccion.value = null

        viewModelScope.launch {
            try {
                var actualizado = evento
                if (nuevoEstado != null) {
                    actualizado = repo.cambiarEstado(evento.id, nuevoEstado)
                    _estado.value = UiState.Exito(actualizado)
                }
                val accion = repo.registrarAccion(evento.id, usuario, descripcion)
                _estado.value =
                    UiState.Exito(actualizado.copy(acciones = actualizado.acciones + accion))
                if (limpiarNota) _nota.value = ""
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorAccion.value = mensajeDeError(e)
            } finally {
                _enviando.value = false
            }
        }
    }
}

class DetalleEventoViewModelFactory(
    private val repo: EventoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        DetalleEventoViewModel(repo) as T
}
