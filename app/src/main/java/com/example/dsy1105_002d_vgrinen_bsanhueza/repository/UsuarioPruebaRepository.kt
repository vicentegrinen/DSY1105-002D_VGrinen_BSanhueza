package com.example.dsy1105_002d_vgrinen_bsanhueza.repository

import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UsuarioPrueba
import com.example.dsy1105_002d_vgrinen_bsanhueza.remote.ApiService
import com.example.dsy1105_002d_vgrinen_bsanhueza.remote.CambioRolRequest

// Usuarios de prueba y roles que administra el rol Admin (vienen de la API REST).
// No confundir con UsuarioRepository, que guarda el registro integral en Room.
class UsuarioPruebaRepository(private val api: ApiService) {

    suspend fun obtenerUsuarios(): List<UsuarioPrueba> = api.obtenerUsuarios()

    // PUT: cambia el rol y devuelve el usuario actualizado
    suspend fun cambiarRol(usuarioId: Int, rol: Rol): UsuarioPrueba =
        api.cambiarRol(usuarioId, CambioRolRequest(rol))
}
