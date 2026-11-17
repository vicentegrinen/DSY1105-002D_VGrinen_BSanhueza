package com.example.dsy1105_002d_vgrinen_bsanhueza.model

// Estado de una pantalla que carga datos desde la API (Guía 14).
// La pantalla dibuja una cosa distinta según el caso: cargando, datos o error.
sealed interface UiState<out T> {

    // Todavía no hay respuesta de la API
    data object Cargando : UiState<Nothing>

    // La API respondió bien
    data class Exito<out T>(val datos: T) : UiState<T>

    // La llamada falló. "datosGuardados" trae lo último que se alcanzó a cargar (si existe),
    // para mostrarlo bajo el aviso "Sin conexión. Mostrando datos guardados."
    data class Error<out T>(
        val mensaje: String,
        val datosGuardados: T? = null
    ) : UiState<T>
}
