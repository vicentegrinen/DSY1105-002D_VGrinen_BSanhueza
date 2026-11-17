package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import com.example.dsy1105_002d_vgrinen_bsanhueza.eventoDePrueba
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.FiltroEventos
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Prioridad
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.EventoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// Guía 15: lista de eventos. Se prueba la carga, el error de red y los filtros
// con un EventoRepository simulado (MockK).
@OptIn(ExperimentalCoroutinesApi::class)
class EventosViewModelTest : StringSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    val critico = eventoDePrueba(id = "EV-0003", titulo = "Puerta forzada")
    val medio = eventoDePrueba(
        id = "EV-0002",
        titulo = "Cámara obstruida",
        prioridad = Prioridad.MEDIA,
        estado = EstadoAtencion.EN_ATENCION
    )
    val atendido = eventoDePrueba(
        id = "EV-0001",
        titulo = "Ronda sin novedad",
        prioridad = Prioridad.BAJA,
        estado = EstadoAtencion.ATENDIDA
    )
    val lista = listOf(critico, medio, atendido)

    // ---- Carga ----

    "antes de cargar el estado es Cargando" {
        EventosViewModel(mockk()).estado.value shouldBe UiState.Cargando
    }

    "al cargar publica la lista que entrega el repositorio" {
        val repo = mockk<EventoRepository>()
        coEvery { repo.obtenerEventos() } returns lista
        val viewModel = EventosViewModel(repo)

        viewModel.cargar()

        viewModel.estado.value shouldBe UiState.Exito(lista)
    }

    // ---- Error de red ----

    "sin conexión y sin copia local queda en Error con el aviso de red" {
        val repo = mockk<EventoRepository>()
        coEvery { repo.obtenerEventos() } throws IOException("sin red")
        coEvery { repo.eventosGuardados() } returns emptyList()
        val viewModel = EventosViewModel(repo)

        viewModel.cargar()

        viewModel.estado.value shouldBe
            UiState.Error("Sin conexión. Revisa tu red e inténtalo de nuevo.", null)
    }

    "sin conexión pero con copia local muestra los datos guardados" {
        val repo = mockk<EventoRepository>()
        coEvery { repo.obtenerEventos() } throws IOException("sin red")
        coEvery { repo.eventosGuardados() } returns lista
        val viewModel = EventosViewModel(repo)

        viewModel.cargar()

        viewModel.estado.value shouldBe
            UiState.Error("Sin conexión. Mostrando datos guardados.", lista)
    }

    "si el servidor responde 500 se informa el código" {
        val repo = mockk<EventoRepository>()
        coEvery { repo.obtenerEventos() } throws
            HttpException(Response.error<Any>(500, "".toResponseBody()))
        coEvery { repo.eventosGuardados() } returns emptyList()
        val viewModel = EventosViewModel(repo)

        viewModel.cargar()

        viewModel.estado.value shouldBe
            UiState.Error("El servidor respondió con un error (500).", null)
    }

    "reintentar después de un error vuelve a mostrar la lista" {
        val repo = mockk<EventoRepository>()
        coEvery { repo.obtenerEventos() } throws IOException("sin red") andThen lista
        coEvery { repo.eventosGuardados() } returns emptyList()
        val viewModel = EventosViewModel(repo)
        viewModel.cargar()

        viewModel.cargar()

        viewModel.estado.value shouldBe UiState.Exito(lista)
    }

    // ---- Filtros ----

    "el filtro Críticas deja solo los eventos críticos sin volver a llamar a la API" {
        val repo = mockk<EventoRepository>()
        coEvery { repo.obtenerEventos() } returns lista
        val viewModel = EventosViewModel(repo)
        viewModel.cargar()

        viewModel.cambiarFiltro(FiltroEventos.CRITICAS)

        viewModel.filtro.value shouldBe FiltroEventos.CRITICAS
        viewModel.estado.value shouldBe UiState.Exito(listOf(critico))
        coVerify(exactly = 1) { repo.obtenerEventos() }
    }

    "los filtros Pendientes y Atendidas separan por estado de atención" {
        val repo = mockk<EventoRepository>()
        coEvery { repo.obtenerEventos() } returns lista
        val viewModel = EventosViewModel(repo)
        viewModel.cargar()

        viewModel.cambiarFiltro(FiltroEventos.PENDIENTES)
        viewModel.estado.value shouldBe UiState.Exito(listOf(critico))

        viewModel.cambiarFiltro(FiltroEventos.ATENDIDAS)
        viewModel.estado.value shouldBe UiState.Exito(listOf(atendido))
    }
})
