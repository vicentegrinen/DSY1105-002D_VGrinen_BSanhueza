package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import retrofit2.HttpException
import java.io.IOException

// Convierte un fallo de la API en el texto que ve el usuario (nunca se usa println).
// IOException: no hubo conexión. HttpException: el servidor respondió con un código de error.
fun mensajeDeError(error: Throwable, hayDatosGuardados: Boolean = false): String {
    val causa = when (error) {
        is IOException -> "Sin conexión."
        is HttpException ->
            if (error.code() == 404) "No se encontró la información solicitada."
            else "El servidor respondió con un error (${error.code()})."
        else -> "Ocurrió un error inesperado."
    }
    return when {
        hayDatosGuardados -> "$causa Mostrando datos guardados."
        error is IOException -> "$causa Revisa tu red e inténtalo de nuevo."
        else -> causa
    }
}
