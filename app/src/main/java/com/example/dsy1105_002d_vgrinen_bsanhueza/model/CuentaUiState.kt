package com.example.dsy1105_002d_vgrinen_bsanhueza.model

// Estado del formulario "Crear cuenta"
data class CuentaUiState(
    val correo: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val username: String = "",
    val pais: String = "Chile",
    val recibirNovedades: Boolean = false
)
