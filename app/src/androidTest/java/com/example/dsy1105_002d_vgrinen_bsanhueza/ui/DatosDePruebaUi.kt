package com.example.dsy1105_002d_vgrinen_bsanhueza.ui

import com.example.dsy1105_002d_vgrinen_bsanhueza.model.AccionEvento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Prioridad

// Evento ficticio para las pruebas de interfaz (Guía 15).
fun eventoDePruebaUi(
    id: String = "EV-0001",
    titulo: String = "Movimiento fuera de horario",
    prioridad: Prioridad = Prioridad.CRITICA,
    estado: EstadoAtencion = EstadoAtencion.PENDIENTE
) = Evento(
    id = id,
    titulo = titulo,
    camaraId = "Cam-03",
    camaraNombre = "Bodega norte",
    ubicacionReferencial = "Piso 1, sector norte",
    prioridad = prioridad,
    fechaHora = "2026-11-09T10:42:00",
    estado = estado,
    acciones = listOf(
        AccionEvento(
            id = 1,
            usuario = AccionEvento.USUARIO_SISTEMA,
            descripcion = "Alerta generada por el sistema",
            fechaHora = "2026-11-09T10:42:00"
        )
    )
)
