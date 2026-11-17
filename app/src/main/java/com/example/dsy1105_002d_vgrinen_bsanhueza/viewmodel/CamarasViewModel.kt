package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Camara
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.CamaraRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Cámaras simuladas con su estado (en línea / sin señal).
class CamarasViewModel(
    private val repo: CamaraRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Camara>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Camara>>> = _estado

    // Última lista cargada con éxito: se muestra bajo el aviso "Sin conexión"
    private var guardadas: List<Camara>? = null

    // La pantalla lo llama al abrirse y desde el botón "Reintentar"
    fun cargar() {
        viewModelScope.launch {
            if (_estado.value !is UiState.Exito) _estado.value = UiState.Cargando
            try {
                val camaras = repo.obtenerCamaras()
                guardadas = camaras
                _estado.value = UiState.Exito(camaras)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // App recién abierta y sin red: se usa la copia guardada en el teléfono
                if (guardadas == null) {
                    guardadas = repo.camarasGuardadas().takeIf { it.isNotEmpty() }
                }
                _estado.value = UiState.Error(mensajeDeError(e, guardadas != null), guardadas)
            }
        }
    }
}

class CamarasViewModelFactory(
    private val repo: CamaraRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CamarasViewModel(repo) as T
}
