package com.example.dsy1105_002d_vgrinen_bsanhueza.model

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val rol: Rol? = null, // rol del usuario autenticado (null si no hay sesión)
    val errores: LoginErrores = LoginErrores()
)
