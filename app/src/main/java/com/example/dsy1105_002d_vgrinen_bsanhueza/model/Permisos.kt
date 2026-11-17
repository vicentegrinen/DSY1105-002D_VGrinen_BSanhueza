package com.example.dsy1105_002d_vgrinen_bsanhueza.model

// Secciones de Modo Guardián (las pestañas de la barra inferior).
enum class Seccion { PANEL, EVENTOS, CAMARAS, USUARIOS, PERFIL }

// El rol define qué opciones se ven, en el orden en que aparecen en la barra inferior.
// La primera sección de cada lista es la pantalla de inicio de ese rol.
// - Administrador: usuarios de prueba y roles.
// - Supervisor: indicadores (panel), eventos y seguimiento, cámaras.
// - Operador: eventos, cámaras y registro de acciones.
fun Rol.secciones(): List<Seccion> = when (this) {
    Rol.ADMIN -> listOf(Seccion.USUARIOS, Seccion.PERFIL)
    Rol.SUPERVISOR -> listOf(Seccion.PANEL, Seccion.EVENTOS, Seccion.CAMARAS, Seccion.PERFIL)
    Rol.OPERADOR -> listOf(Seccion.EVENTOS, Seccion.CAMARAS, Seccion.PERFIL)
}

// Solo el Operador registra acciones sobre un evento (tomar, escalar, marcar como atendido).
// El Supervisor hace seguimiento: ve el evento y su historial, sin modificarlo.
val Rol.puedeRegistrarAcciones: Boolean
    get() = this == Rol.OPERADOR
