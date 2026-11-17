package com.example.dsy1105_002d_vgrinen_bsanhueza

import com.example.dsy1105_002d_vgrinen_bsanhueza.model.AccionEvento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Prioridad

// Datos ficticios que comparten las pruebas unitarias (Guía 15).

const val OPERADOR_DE_PRUEBA = "operador@guardian.test"

fun accionDePrueba(
    id: Int = 1,
    usuario: String = AccionEvento.USUARIO_SISTEMA,
    descripcion: String = "Alerta generada por el sistema",
    fechaHora: String = "2026-11-09T10:42:00"
) = AccionEvento(id, usuario, descripcion, fechaHora)

fun eventoDePrueba(
    id: String = "EV-0001",
    titulo: String = "Movimiento fuera de horario",
    prioridad: Prioridad = Prioridad.CRITICA,
    estado: EstadoAtencion = EstadoAtencion.PENDIENTE,
    acciones: List<AccionEvento> = listOf(accionDePrueba())
) = Evento(
    id = id,
    titulo = titulo,
    camaraId = "Cam-03",
    camaraNombre = "Bodega norte",
    ubicacionReferencial = "Piso 1, sector norte",
    prioridad = prioridad,
    fechaHora = "2026-11-09T10:42:00",
    estado = estado,
    acciones = acciones
)
