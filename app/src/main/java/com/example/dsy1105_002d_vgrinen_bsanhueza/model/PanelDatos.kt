package com.example.dsy1105_002d_vgrinen_bsanhueza.model

// Lo que muestra el panel operacional: las 4 tarjetas y las alertas más urgentes.
data class PanelDatos(
    val indicadores: Indicadores,
    val alertasRecientes: List<Evento>
)
