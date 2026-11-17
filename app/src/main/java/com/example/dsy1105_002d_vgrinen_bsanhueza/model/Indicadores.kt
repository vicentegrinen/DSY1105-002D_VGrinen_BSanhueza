package com.example.dsy1105_002d_vgrinen_bsanhueza.model

// Indicadores del panel operacional (los 4 valores de las tarjetas).
data class Indicadores(
    val eventosHoy: Int,
    val alertasPendientes: Int,          // eventos de hoy que aún no están atendidos
    val atendidas: Int,
    val minutosRespuestaPromedio: Int    // tiempo promedio entre la alerta y su cierre
)
