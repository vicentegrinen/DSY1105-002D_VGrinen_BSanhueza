package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import androidx.lifecycle.ViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.LoginErrores
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.LoginUiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

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



    fun iniciarSesion(): Boolean {
        val actual = _estado.value

        val errores = LoginErrores(
            correo = validarCorreo(actual.correo),
            clave = validarClave(actual.clave)
        )


        if (errores.correo != null || errores.clave != null) {
            _estado.update { it.copy(errores = errores) }
            return false
        }

        val usuario = USUARIOS.firstOrNull {
            it.correo.equals(actual.correo.trim(), ignoreCase = true) &&
                it.clave == actual.clave
        }

        if (usuario == null) {
            _estado.update {
                it.copy(errores = LoginErrores(general = "Correo o contraseña incorrectos"))
            }
            return false
        }
        _estado.update { it.copy(clave = "", rol = usuario.rol, errores = LoginErrores()) }
        return true
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
