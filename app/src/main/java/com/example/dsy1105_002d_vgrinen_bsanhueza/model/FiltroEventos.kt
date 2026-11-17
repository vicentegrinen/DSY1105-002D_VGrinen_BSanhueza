package com.example.dsy1105_002d_vgrinen_bsanhueza.model

// Chips de filtro de la pantalla "Eventos y alertas".
enum class FiltroEventos(val etiqueta: String) {
    TODOS("Todos"),
    CRITICAS("Críticas"),
    PENDIENTES("Pendientes"),
    ATENDIDAS("Atendidas");

    // Devuelve solo los eventos que corresponden a este filtro
    fun aplicar(eventos: List<Evento>): List<Evento> = when (this) {
        TODOS -> eventos
        CRITICAS -> eventos.filter { it.prioridad == Prioridad.CRITICA }
        PENDIENTES -> eventos.filter { it.estado == EstadoAtencion.PENDIENTE }
        ATENDIDAS -> eventos.filter { it.estado == EstadoAtencion.ATENDIDA }
    }
}
