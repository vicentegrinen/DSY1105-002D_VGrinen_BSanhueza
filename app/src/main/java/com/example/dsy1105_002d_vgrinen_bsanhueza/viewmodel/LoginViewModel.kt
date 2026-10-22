package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.LoginErrores
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.LoginUiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Usuario
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.CuentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val cuentaRepo: CuentaRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado

    fun onCorreoChange(valor: String) {
        _estado.update {
            val error = if (it.errores.correo != null) validarCorreo(valor) else null
            it.copy(
                correo = valor,
                errores = it.errores.copy(correo = error, general = null)
            )
        }
    }

    fun onClaveChange(valor: String) {
        _estado.update {
            val error = if (it.errores.clave != null) validarClave(valor) else null
            it.copy(
                clave = valor,
                errores = it.errores.copy(clave = error, general = null)
            )
        }
    }

    // Valida el formulario y comprueba las credenciales: primero contra los usuarios de prueba
    // y luego contra las cuentas creadas en "Crear cuenta" (Room). Llama onExito si coinciden.
    fun iniciarSesion(onExito: () -> Unit) {
        val actual = _estado.value

        val errores = LoginErrores(
            correo = validarCorreo(actual.correo),
            clave = validarClave(actual.clave)
        )

        if (errores.correo != null || errores.clave != null) {
            _estado.update { it.copy(errores = errores) }
            return
        }

        viewModelScope.launch {
            val usuarioPrueba = USUARIOS.firstOrNull {
                it.correo.equals(actual.correo.trim(), ignoreCase = true) &&
                    it.clave == actual.clave
            }

            val rol: Rol? = usuarioPrueba?.rol
                ?: cuentaRepo.autenticar(actual.correo, actual.clave)?.let { Rol.OPERADOR }

            if (rol == null) {
                _estado.update {
                    it.copy(errores = LoginErrores(general = "Correo o contraseña incorrectos"))
                }
                return@launch
            }

            _estado.update { it.copy(clave = "", rol = rol, errores = LoginErrores()) }
            onExito()
        }
    }

    fun cerrarSesion() {
        _estado.value = LoginUiState()
    }

    private fun validarCorreo(valor: String): String? {
        val v = valor.trim()
        return when {
            v.isEmpty() -> "Campo obligatorio"
            !REGEX_CORREO.matches(v) -> "Correo inválido (ej: nombre@dominio.cl)"
            else -> null
        }
    }

    private fun validarClave(valor: String): String? =
        if (valor.isEmpty()) "Campo obligatorio" else null

    private companion object {

        val USUARIOS = listOf(
            Usuario("admin@guardian.test", "123456", Rol.ADMIN),
            Usuario("supervisor@guardian.test", "123456", Rol.SUPERVISOR),
            Usuario("operador@guardian.test", "123456", Rol.OPERADOR)
        )

        val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}

class LoginViewModelFactory(
    private val cuentaRepo: CuentaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        LoginViewModel(cuentaRepo) as T
}
