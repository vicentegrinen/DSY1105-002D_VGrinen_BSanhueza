package com.example.dsy1105_002d_vgrinen_bsanhueza.model

// Evento o alerta de seguridad detectado por una cámara. Todos los datos son ficticios.
data class Evento(
    val id: String,                   // por ejemplo "EV-0412"
    val titulo: String,
    val camaraId: String,             // por ejemplo "Cam-03"
    val camaraNombre: String,         // por ejemplo "Bodega norte"
    val ubicacionReferencial: String, // por ejemplo "Piso 1, sector norte"
    val prioridad: Prioridad,
    val fechaHora: String,            // ISO local: "2026-11-09T10:42:00"
    val estado: EstadoAtencion,
    val acciones: List<AccionEvento>  // registro de acciones (quién, qué y cuándo)
) {
    val hora: String get() = horaCorta(fechaHora)
}
