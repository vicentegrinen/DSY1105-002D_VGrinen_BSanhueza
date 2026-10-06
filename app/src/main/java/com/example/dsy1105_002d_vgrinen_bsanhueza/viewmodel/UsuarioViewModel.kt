package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import androidx.lifecycle.ViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UsuarioErrores
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel : ViewModel() {

    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado

    // ---------- Cambios de campos ----------
    // Si el campo ya tenía un error, se revalida mientras el usuario escribe
    // (así el mensaje se actualiza o desaparece en tiempo real).

    fun onNombreChange(valor: String) {
        _estado.update {
            val error = if (it.errores.nombre != null) validarNombre(valor) else null
            it.copy(nombre = valor, errores = it.errores.copy(nombre = error))
        }
    }

    fun onCorreoChange(valor: String) {
        _estado.update {
            val error = if (it.errores.correo != null) validarCorreo(valor) else null
            it.copy(correo = valor, errores = it.errores.copy(correo = error))
        }
    }

    fun onClaveChange(valor: String) {
        _estado.update {
            val error = if (it.errores.clave != null) validarClave(valor) else null
            it.copy(clave = valor, errores = it.errores.copy(clave = error))
        }
    }

    fun onDireccionChange(valor: String) {
        _estado.update {
            val error = if (it.errores.direccion != null) validarDireccion(valor) else null
            it.copy(direccion = valor, errores = it.errores.copy(direccion = error))
        }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update {
            it.copy(aceptaTerminos = valor, errores = it.errores.copy(terminos = null))
        }
    }

    // ---------- Validación global ----------

    fun validarFormulario(): Boolean {
        val actual = _estado.value

        val errores = UsuarioErrores(
            nombre = validarNombre(actual.nombre),
            correo = validarCorreo(actual.correo),
            clave = validarClave(actual.clave),
            direccion = validarDireccion(actual.direccion),
            terminos = validarTerminos(actual.aceptaTerminos)
        )

        _estado.update { it.copy(errores = errores) }

        return listOf(
            errores.nombre, errores.correo, errores.clave,
            errores.direccion, errores.terminos
        ).all { it == null }
    }


    private fun validarNombre(valor: String): String? {
        val v = valor.trim()
        return when {
            v.isEmpty() -> "Campo obligatorio"
            v.length < 2 -> "Debe tener al menos 2 caracteres"
            v.length > 50 -> "Máximo 50 caracteres"
            !REGEX_NOMBRE.matches(v) -> "Solo se permiten letras y espacios"
            else -> null
        }
    }

    private fun validarCorreo(valor: String): String? {
        val v = valor.trim()
        return when {
            v.isEmpty() -> "Campo obligatorio"
            !REGEX_CORREO.matches(v) -> "Correo inválido (ej: nombre@dominio.cl)"
            else -> null
        }
    }

    private fun validarClave(valor: String): String? = when {
        valor.isEmpty() -> "Campo obligatorio"
        valor.length < MIN_CLAVE -> "Debe tener al menos $MIN_CLAVE caracteres"
        valor.length > 32 -> "Máximo 32 caracteres"
        valor.any { it.isWhitespace() } -> "No puede contener espacios"
        !valor.any { it.isLetter() } -> "Debe incluir al menos una letra"
        !valor.any { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }

    private fun validarDireccion(valor: String): String? {
        val v = valor.trim()
        return when {
            v.isEmpty() -> "Campo obligatorio"
            v.length < 5 -> "Ingresa una dirección más completa"
            v.length > 100 -> "Máximo 100 caracteres"
            else -> null
        }
    }

    private fun validarTerminos(acepta: Boolean): String? =
        if (!acepta) "Debes aceptar los términos y condiciones" else null

    private companion object {
        const val MIN_CLAVE = 6 // súbelo a 8 si tu pauta lo permite
        val REGEX_NOMBRE = Regex("^\\p{L}+([ '\\-]\\p{L}+)*$")
        val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}