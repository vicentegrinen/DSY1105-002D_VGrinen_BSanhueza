package com.example.dsy1105_002d_vgrinen_bsanhueza.repository

import com.example.dsy1105_002d_vgrinen_bsanhueza.data.GuardianCacheDao
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.aEntidad
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.aModelo
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Camara
import com.example.dsy1105_002d_vgrinen_bsanhueza.remote.ApiService

// Mediador entre la API REST y el ViewModel de cámaras.
// "cache" es opcional: si se entrega, la última respuesta se guarda en Room.
class CamaraRepository(
    private val api: ApiService,
    private val cache: GuardianCacheDao? = null
) {

    suspend fun obtenerCamaras(): List<Camara> {
        val camaras = api.obtenerCamaras()
        cache?.reemplazarCamaras(camaras.map { it.aEntidad() })
        return camaras
    }

    // Última lista guardada en el teléfono (vacía si nunca se cargó)
    suspend fun camarasGuardadas(): List<Camara> =
        cache?.obtenerCamaras()?.map { it.aModelo() } ?: emptyList()
}
