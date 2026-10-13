package com.example.dsy1105_002d_vgrinen_bsanhueza.model

data class LoginErrores(
    val correo: String? = null,
    val clave: String? = null,
    val general: String? = null // credenciales incorrectas
)
