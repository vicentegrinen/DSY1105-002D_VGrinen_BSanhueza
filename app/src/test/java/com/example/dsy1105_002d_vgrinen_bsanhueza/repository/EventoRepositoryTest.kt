package com.example.dsy1105_002d_vgrinen_bsanhueza.repository

import com.example.dsy1105_002d_vgrinen_bsanhueza.OPERADOR_DE_PRUEBA
import com.example.dsy1105_002d_vgrinen_bsanhueza.accionDePrueba
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.GuardianCacheDao
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.aEntidad
import com.example.dsy1105_002d_vgrinen_bsanhueza.eventoDePrueba
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Indicadores
import com.example.dsy1105_002d_vgrinen_bsanhueza.remote.ApiService
import com.example.dsy1105_002d_vgrinen_bsanhueza.remote.CambioEstadoRequest
import com.example.dsy1105_002d_vgrinen_bsanhueza.remote.NuevaAccionRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException

// Guía 15: el repositorio recibe ApiService por constructor, así que aquí se reemplaza
// por un mock de MockK (coEvery) y no se hace ninguna llamada de red real.
class EventoRepositoryTest : StringSpec({

    val eventos = listOf(
        eventoDePrueba(id = "EV-0002", titulo = "Puerta forzada"),
        eventoDePrueba(id = "EV-0001", titulo = "Movimiento fuera de horario")
    )

    // ---- GET ----

    "obtenerEventos devuelve la lista que entrega la API" {
        val api = mockk<ApiService>()
        coEvery { api.obtenerEventos() } returns eventos

        EventoRepository(api).obtenerEventos() shouldBe eventos
    }

    "obtenerEvento pide a la API el evento por su id" {
        val api = mockk<ApiService>()
        coEvery { api.obtenerEvento("EV-0002") } returns eventos.first()

        EventoRepository(api).obtenerEvento("EV-0002") shouldBe eventos.first()
    }

    "obtenerIndicadores devuelve los indicadores de la API" {
        val api = mockk<ApiService>()
        val indicadores = Indicadores(
            eventosHoy = 8,
            alertasPendientes = 3,
            atendidas = 5,
            minutosRespuestaPromedio = 12
        )
        coEvery { api.obtenerIndicadores() } returns indicadores

        EventoRepository(api).obtenerIndicadores() shouldBe indicadores
    }

    "un error de red de la API llega sin modificar a quien llama" {
        val api = mockk<ApiService>()
        coEvery { api.obtenerEventos() } throws IOException("sin red")

        shouldThrow<IOException> { EventoRepository(api).obtenerEventos() }
    }

    // ---- POST y PUT ----

    "registrarAccion envía el POST con el usuario y la descripción sin espacios sobrantes" {
        val api = mockk<ApiService>()
        val accion = accionDePrueba(2, OPERADOR_DE_PRUEBA, "Tomó el evento", "2026-11-09T10:50:00")
        coEvery { api.registrarAccion(any(), any()) } returns accion

        val resultado = EventoRepository(api)
            .registrarAccion("EV-0001", " $OPERADOR_DE_PRUEBA ", " Tomó el evento ")

        resultado shouldBe accion
        coVerify {
            api.registrarAccion("EV-0001", NuevaAccionRequest(OPERADOR_DE_PRUEBA, "Tomó el evento"))
        }
    }

    "cambiarEstado envía el PUT con el nuevo estado y devuelve el evento actualizado" {
        val api = mockk<ApiService>()
        val atendido = eventoDePrueba(estado = EstadoAtencion.ATENDIDA)
        coEvery {
            api.cambiarEstado("EV-0001", CambioEstadoRequest(EstadoAtencion.ATENDIDA))
        } returns atendido

        EventoRepository(api).cambiarEstado("EV-0001", EstadoAtencion.ATENDIDA) shouldBe atendido
    }

    // ---- Copia local ----

    "cada carga correcta se guarda en la copia local" {
        val api = mockk<ApiService>()
        val cache = mockk<GuardianCacheDao>(relaxUnitFun = true)
        coEvery { api.obtenerEventos() } returns eventos

        EventoRepository(api, cache).obtenerEventos()

        coVerify { cache.reemplazarEventos(eventos.map { it.aEntidad() }) }
    }

    "eventosGuardados reconstruye los eventos desde la copia local" {
        val cache = mockk<GuardianCacheDao>()
        coEvery { cache.obtenerEventos() } returns eventos.map { it.aEntidad() }

        EventoRepository(mockk(), cache).eventosGuardados() shouldBe eventos
    }

    "sin copia local eventosGuardados devuelve una lista vacía" {
        EventoRepository(mockk()).eventosGuardados() shouldBe emptyList<Evento>()
    }
})
