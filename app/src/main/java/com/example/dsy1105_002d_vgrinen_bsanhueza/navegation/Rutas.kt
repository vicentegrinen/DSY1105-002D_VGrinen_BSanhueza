package com.example.dsy1105_002d_vgrinen_bsanhueza.navegation

import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Seccion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.secciones

// Rutas de Modo Guardián. Las rutas anteriores ("crear_cuenta", "perfil", "registro", etc.)
// siguen escritas como texto en AppNavigation, tal como estaban.
object Rutas {
    const val LOGIN = "login"

    // Pestañas de la barra inferior
    const val PANEL = "panel"
    const val EVENTOS = "eventos"
    const val CAMARAS = "camaras"
    const val USUARIOS = "usuarios"   // solo rol Admin
    const val MI_PERFIL = "mi_perfil" // "perfil" ya existe: es la pantalla de cámara y galería

    // Detalle de un evento: el id viaja en la ruta
    const val ARG_EVENTO_ID = "eventoId"
    const val DETALLE_EVENTO = "evento/{$ARG_EVENTO_ID}"

    fun detalleEvento(id: String) = "evento/$id"

    // Ruta de cada sección de la barra inferior
    fun de(seccion: Seccion): String = when (seccion) {
        Seccion.PANEL -> PANEL
        Seccion.EVENTOS -> EVENTOS
        Seccion.CAMARAS -> CAMARAS
        Seccion.USUARIOS -> USUARIOS
        Seccion.PERFIL -> MI_PERFIL
    }

    // Pantalla de inicio según el rol: la primera sección que ese rol puede ver.
    // Admin -> Usuarios, Supervisor -> Panel, Operador -> Eventos. Sin rol -> login.
    fun inicioPara(rol: Rol?): String =
        rol?.secciones()?.firstOrNull()?.let { de(it) } ?: LOGIN
}
