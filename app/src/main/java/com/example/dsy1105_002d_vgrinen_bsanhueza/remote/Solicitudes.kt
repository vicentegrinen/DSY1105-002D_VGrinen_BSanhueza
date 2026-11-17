package com.example.dsy1105_002d_vgrinen_bsanhueza.remote

import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol

// Cuerpos (JSON) que la app envía a la API.

// POST eventos/{id}/acciones: la API asigna el id y la fecha/hora de la acción.
data class NuevaAccionRequest(
    val usuario: String,
    val descripcion: String
)

// PUT eventos/{id}/estado
data class CambioEstadoRequest(
    val estado: EstadoAtencion
)

// PUT usuarios/{id}/rol (solo Admin)
data class CambioRolRequest(
    val rol: Rol
)
