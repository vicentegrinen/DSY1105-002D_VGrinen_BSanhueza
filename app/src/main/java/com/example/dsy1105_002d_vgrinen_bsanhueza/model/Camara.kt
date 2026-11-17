package com.example.dsy1105_002d_vgrinen_bsanhueza.model

// Cámara simulada del recinto.
data class Camara(
    val id: String,              // por ejemplo "Cam-07"
    val nombre: String,          // por ejemplo "Acceso principal"
    val enLinea: Boolean,        // true: "En línea" / false: "Sin señal"
    val ultimaSenal: String?,    // ISO local; solo viene cuando la cámara está sin señal
    val alertasPendientes: Int   // eventos de esta cámara que aún no están atendidos
) {
    val horaUltimaSenal: String? get() = ultimaSenal?.let { horaCorta(it) }
}
