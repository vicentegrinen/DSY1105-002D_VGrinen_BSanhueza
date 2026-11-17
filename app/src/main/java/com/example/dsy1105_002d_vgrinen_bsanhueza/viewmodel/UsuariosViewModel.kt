package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UsuarioPrueba
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.UsuarioPruebaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Administración de usuarios de prueba y roles (solo rol Admin).
class UsuariosViewModel(
    private val repo: UsuarioPruebaRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<UsuarioPrueba>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<UsuarioPrueba>>> = _estado

    // true mientras se envía un cambio de rol: la pantalla deshabilita los chips
    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando

    // Error del último cambio de rol (PUT); null si no hubo
    private val _errorAccion = MutableStateFlow<String?>(null)
    val errorAccion: StateFlow<String?> = _errorAccion

    // Última lista cargada con éxito: se muestra si una recarga posterior falla
    private var guardados: List<UsuarioPrueba>? = null

    fun cargar() {
        viewModelScope.launch {
            if (_estado.value !is UiState.Exito) _estado.value = UiState.Cargando
            try {
                val usuarios = repo.obtenerUsuarios()
                guardados = usuarios
                _estado.value = UiState.Exito(usuarios)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.value = UiState.Error(mensajeDeError(e, guardados != null), guardados)
            }
        }
    }

    fun cambiarRol(usuario: UsuarioPrueba, nuevoRol: Rol) {
        val actuales = (_estado.value as? UiState.Exito)?.datos ?: return
        if (usuario.rol == nuevoRol || _guardando.value) return
        _guardando.value = true
        _errorAccion.value = null

        viewModelScope.launch {
            try {
                val actualizado = repo.cambiarRol(usuario.id, nuevoRol)
                val lista = actuales.map { if (it.id == actualizado.id) actualizado else it }
                guardados = lista
                _estado.value = UiState.Exito(lista)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorAccion.value = mensajeDeError(e)
            } finally {
                _guardando.value = false
            }
        }
    }

    fun descartarErrorAccion() {
        _errorAccion.value = null
    }
}

class UsuariosViewModelFactory(
    private val repo: UsuarioPruebaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        UsuariosViewModel(repo) as T
}
