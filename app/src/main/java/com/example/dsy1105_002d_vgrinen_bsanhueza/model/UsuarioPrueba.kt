package com.example.dsy1105_002d_vgrinen_bsanhueza.model

// Usuario ficticio que administra el rol Admin. No lleva contraseña:
// la API nunca la entrega. No confundir con Usuario (credenciales de prueba del login).
data class UsuarioPrueba(
    val id: Int,
    val nombre: String,
    val correo: String,
    val rol: Rol
)
