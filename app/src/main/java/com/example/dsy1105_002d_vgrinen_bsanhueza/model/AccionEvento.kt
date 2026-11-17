package com.example.dsy1105_002d_vgrinen_bsanhueza.model

// Una entrada del historial de un evento: quién hizo qué y cuándo.
data class AccionEvento(
    val id: Int,
    val usuario: String,      // quién (usuario ficticio o "sistema")
    val descripcion: String,  // qué
    val fechaHora: String     // cuándo (ISO local, lo asigna la API)
) {
    val hora: String get() = horaCorta(fechaHora)

    // true cuando la acción la generó el sistema (por ejemplo, la alerta inicial)
    val esDelSistema: Boolean get() = usuario == USUARIO_SISTEMA

    companion object {
        const val USUARIO_SISTEMA = "sistema"
    }
}
