package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.PreferencesManager
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.UsuarioEntity
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UsuarioErrores
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UsuarioUiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class UsuarioViewModel(
    private val repo: UsuarioRepository,
    private val prefs: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    private val _errores = MutableStateFlow(UsuarioErrores())
    val errores: StateFlow<UsuarioErrores> = _errores.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensajeExito = MutableStateFlow(false)
    val mensajeExito: StateFlow<Boolean> = _mensajeExito.asStateFlow()

    val usuarios = repo.usuarios

    init {
        viewModelScope.launch {
            delay(1500) // simula la carga inicial
            val modo = prefs.modoEspecialFlow.first()
            _uiState.update { it.copy(modoEspecial = modo) }
            _cargando.value = false
        }
    }

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v) }
    fun onCorreoChange(v: String) = _uiState.update { it.copy(correo = v) }
    fun onClaveChange(v: String) = _uiState.update { it.copy(clave = v) }
    fun onConfirmarClaveChange(v: String) = _uiState.update { it.copy(confirmarClave = v) }
    fun onDireccionChange(v: String) = _uiState.update { it.copy(direccion = v) }
    fun onTerminosChange(v: Boolean) = _uiState.update { it.copy(aceptaTerminos = v) }
    fun actualizarFoto(uri: Uri?) = _uiState.update { it.copy(fotoUri = uri) }

    fun toggleModoEspecial() {
        viewModelScope.launch {
            val nuevo = !_uiState.value.modoEspecial
            prefs.guardarModoEspecial(nuevo)
            _uiState.update { it.copy(modoEspecial = nuevo) }
            _mensajeExito.value = true
            delay(2000)
            _mensajeExito.value = false
        }
    }

    fun validarFormulario(): Boolean {
        val s = _uiState.value
        val e = UsuarioErrores(
            nombre = if (s.nombre.trim().length < 3) "Mínimo 3 caracteres" else null,
            correo = if (!s.correo.contains("@")) "El correo debe contener @" else null,
            clave = if (s.clave.length < 6) "Mínimo 6 caracteres" else null,
            confirmarClave = when {
                s.confirmarClave.isEmpty() -> "Confirma tu contraseña"
                s.confirmarClave != s.clave -> "Las contraseñas no coinciden"
                else -> null
            },
            direccion = if (s.direccion.isBlank()) "La dirección es obligatoria" else null,
            terminos = if (!s.aceptaTerminos) "Debes aceptar los términos" else null,
            foto = if (s.fotoUri == null) "Selecciona una foto" else null
        )
        _errores.value = e
        return e == UsuarioErrores()
    }

    // Limpia el formulario para un nuevo registro, conservando el modo premium (persistido en DataStore)
    fun reiniciarFormulario() {
        _uiState.update { UsuarioUiState(modoEspecial = it.modoEspecial) }
        _errores.value = UsuarioErrores()
    }

    fun guardarEnRoom() {
        val s = _uiState.value
        viewModelScope.launch {
            repo.insertar(
                UsuarioEntity(
                    nombre = s.nombre.trim(),
                    correo = s.correo.trim(),
                    direccion = s.direccion.trim(),
                    modoEspecial = s.modoEspecial,
                    fotoPath = s.fotoUri?.toString(),
                    fechaRegistro = Date()
                )
            )
        }
    }
}

class UsuarioViewModelFactory(
    private val repo: UsuarioRepository,
    private val prefs: PreferencesManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        UsuarioViewModel(repo, prefs) as T
}