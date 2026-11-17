package com.example.dsy1105_002d_vgrinen_bsanhueza.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

// Regla del caso: el rol define qué opciones se ven.
class PermisosTest : StringSpec({

    "el Administrador ve usuarios y perfil, sin eventos ni cámaras" {
        Rol.ADMIN.secciones() shouldContainExactly listOf(Seccion.USUARIOS, Seccion.PERFIL)
    }

    "el Supervisor ve indicadores, eventos y cámaras" {
        Rol.SUPERVISOR.secciones() shouldContainExactly
            listOf(Seccion.PANEL, Seccion.EVENTOS, Seccion.CAMARAS, Seccion.PERFIL)
    }

    "el Operador ve eventos y cámaras, sin el panel de indicadores" {
        Rol.OPERADOR.secciones() shouldContainExactly
            listOf(Seccion.EVENTOS, Seccion.CAMARAS, Seccion.PERFIL)
    }

    "solo el Operador puede registrar acciones sobre un evento" {
        Rol.OPERADOR.puedeRegistrarAcciones shouldBe true
        Rol.SUPERVISOR.puedeRegistrarAcciones shouldBe false
        Rol.ADMIN.puedeRegistrarAcciones shouldBe false
    }
})
