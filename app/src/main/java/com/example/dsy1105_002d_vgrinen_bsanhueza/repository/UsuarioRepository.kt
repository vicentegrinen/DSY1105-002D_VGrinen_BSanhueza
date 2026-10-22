package com.example.dsy1105_002d_vgrinen_bsanhueza.repository

import com.example.dsy1105_002d_vgrinen_bsanhueza.data.UsuarioDao
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.UsuarioEntity
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val dao: UsuarioDao) {

    val usuarios: Flow<List<UsuarioEntity>> = dao.getUsuariosFlow()

    suspend fun insertar(usuario: UsuarioEntity) = dao.insertar(usuario)
}