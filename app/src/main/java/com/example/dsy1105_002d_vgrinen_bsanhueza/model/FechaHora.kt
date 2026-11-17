package com.example.dsy1105_002d_vgrinen_bsanhueza.model

// La API entrega fecha y hora en formato ISO local, por ejemplo "2026-11-09T10:42:00".
// Devuelve solo la hora y los minutos ("10:42") para mostrarla en las tarjetas.
fun horaCorta(fechaHora: String): String =
    fechaHora.substringAfter('T', missingDelimiterValue = "").take(5)
