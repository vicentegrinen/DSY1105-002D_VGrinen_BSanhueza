package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.CuentaErrores
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.CuentaUiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.CuentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroCuentaViewModel(
    private val repo: CuentaRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(CuentaUiState())
    val estado: StateFlow<CuentaUiState> = _estado.asStateFlow()

    private val _errores = MutableStateFlow(CuentaErrores())
    val errores: StateFlow<CuentaErrores> = _errores.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    // Igual que en el login: si el campo ya tiene error, se revalida mientras el usuario escribe
    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor) }
        if (_errores.value.correo != null) {
            _errores.update { it.copy(correo = validarCorreo(valor)) }
        }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor) }
        _errores.update {
            it.copy(
                clave = if (it.clave != null) validarClave(valor) else null,
                confirmarClave = if (it.confirmarClave != null)
                    validarConfirmacion(valor, _estado.value.confirmarClave) else null
            )
        }
    }

    fun onConfirmarClaveChange(valor: String) {
        _estado.update { it.copy(confirmarClave = valor) }
        if (_errores.value.confirmarClave != null) {
            _errores.update { it.copy(confirmarClave = validarConfirmacion(_estado.value.clave, valor)) }
        }
    }

    fun onUsernameChange(valor: String) {
        _estado.update { it.copy(username = valor) }
        if (_errores.value.username != null) {
            _errores.update { it.copy(username = validarUsername(valor)) }
        }
    }

    fun onPaisChange(valor: String) = _estado.update { it.copy(pais = valor) }

    fun onNovedadesChange(valor: Boolean) = _estado.update { it.copy(recibirNovedades = valor) }

    // Valida el formulario; si es válido y no hay duplicados, guarda en Room y llama onExito
    fun registrar(onExito: () -> Unit) {
        if (_guardando.value) return
        val s = _estado.value

        val errores = CuentaErrores(
            correo = validarCorreo(s.correo),
            clave = validarClave(s.clave),
            confirmarClave = validarConfirmacion(s.clave, s.confirmarClave),
            username = validarUsername(s.username)
        )
        _errores.value = errores
        if (errores != CuentaErrores()) return

        viewModelScope.launch {
            _guardando.value = true
            try {
                val correoOcupado = repo.correoExiste(s.correo)
                val usernameOcupado = repo.usernameExiste(s.username)
                if (correoOcupado || usernameOcupado) {
                    _errores.value = CuentaErrores(
                        correo = if (correoOcupado) "Este correo ya está registrado" else null,
                        username = if (usernameOcupado) "Ese nombre de usuario ya está en uso" else null
                    )
                    return@launch
                }
                repo.registrar(s.correo, s.username, s.clave, s.pais, s.recibirNovedades)
                _estado.value = CuentaUiState()
                _errores.value = CuentaErrores()
                onExito()
            } catch (e: SQLiteConstraintException) {
                _errores.value = CuentaErrores(correo = "Este correo o usuario ya está registrado")
            } finally {
                _guardando.value = false
            }
        }
    }

    // ---------- Reglas de validación (devuelven el mensaje de error o null si es válido) ----------

    private fun validarCorreo(valor: String): String? {
        val v = valor.trim()
        return when {
            v.isEmpty() -> "Campo obligatorio"
            !REGEX_CORREO.matches(v) -> "Correo inválido (ej: nombre@dominio.cl)"
            else -> null
        }
    }

    // Mínimo 15 caracteres, O mínimo 8 con un número y una minúscula
    private fun validarClave(valor: String): String? = when {
        valor.isEmpty() -> "Campo obligatorio"
        valor.length >= 15 -> null
        valor.length >= 8 && valor.any { it.isDigit() } && valor.any { it.isLowerCase() } -> null
        else -> "Mínimo 15 caracteres, o 8 con al menos un número y una minúscula"
    }

    private fun validarConfirmacion(clave: String, confirmacion: String): String? = when {
        confirmacion.isEmpty() -> "Confirma tu contraseña"
        confirmacion != clave -> "Las contraseñas no coinciden"
        else -> null
    }

    // Solo letras y números, con guiones simples; no puede empezar ni terminar con guion
    private fun validarUsername(valor: String): String? = when {
        valor.isEmpty() -> "Campo obligatorio"
        !REGEX_USERNAME.matches(valor) ->
            "Solo letras, números y guiones simples; no puede empezar ni terminar con guion"
        else -> null
    }

    private companion object {
        val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        val REGEX_USERNAME = Regex("^[A-Za-z0-9]+(-[A-Za-z0-9]+)*$")
    }
}

class RegistroCuentaViewModelFactory(
    private val repo: CuentaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        RegistroCuentaViewModel(repo) as T
}
