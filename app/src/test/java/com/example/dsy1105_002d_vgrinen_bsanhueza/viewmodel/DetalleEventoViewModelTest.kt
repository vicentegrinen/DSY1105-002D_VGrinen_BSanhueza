package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import com.example.dsy1105_002d_vgrinen_bsanhueza.OPERADOR_DE_PRUEBA
import com.example.dsy1105_002d_vgrinen_bsanhueza.accionDePrueba
import com.example.dsy1105_002d_vgrinen_bsanhueza.eventoDePrueba
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.EventoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.io.IOException

// Guía 15: detalle del evento. Se prueba la carga, el error de red y el cambio de
// estado (PUT) con su registro de acción (POST), usando un EventoRepository simulado.
@OptIn(ExperimentalCoroutinesApi::class)
class DetalleEventoViewModelTest : StringSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    val pendiente = eventoDePrueba(id = "EV-0001", estado = EstadoAtencion.PENDIENTE)

    // Repositorio que ya entrega el evento pendiente al cargar el detalle
    fun repoConEvento(): EventoRepository {
        val repo = mockk<EventoRepository>()
        coEvery { repo.obtenerEvento("EV-0001") } returns pendiente
        return repo
    }

    // ---- Carga y error de red ----

    "al cargar muestra el evento pedido" {
        val viewModel = DetalleEventoViewModel(repoConEvento())

        viewModel.cargar("EV-0001")

        viewModel.estado.value shouldBe UiState.Exito(pendiente)
    }

    "sin conexión el detalle queda en Error y al reintentar se recupera" {
        val repo = mockk<EventoRepository>()
        coEvery { repo.obtenerEvento("EV-0001") } throws IOException("sin red") andThen pendiente
        val viewModel = DetalleEventoViewModel(repo)

        viewModel.cargar("EV-0001")
        viewModel.estado.value shouldBe
            UiState.Error("Sin conexión. Revisa tu red e inténtalo de nuevo.", null)

        viewModel.reintentar()
        viewModel.estado.value shouldBe UiState.Exito(pendiente)
    }

    // ---- Cambio de estado ----

    "tomar el evento lo pasa a En atención y registra quién lo hizo" {
        val repo = repoConEvento()
        val enAtencion = pendiente.copy(estado = EstadoAtencion.EN_ATENCION)
        val accion = accionDePrueba(2, OPERADOR_DE_PRUEBA, "Tomó el evento", "2026-11-09T10:50:00")
        coEvery { repo.cambiarEstado("EV-0001", EstadoAtencion.EN_ATENCION) } returns enAtencion
        coEvery { repo.registrarAccion("EV-0001", OPERADOR_DE_PRUEBA, "Tomó el evento") } returns accion
        val viewModel = DetalleEventoViewModel(repo)
        viewModel.cargar("EV-0001")

        viewModel.tomarEvento(OPERADOR_DE_PRUEBA)

        val evento = (viewModel.estado.value as UiState.Exito).datos
        evento.estado shouldBe EstadoAtencion.EN_ATENCION
        evento.acciones.last() shouldBe accion
        viewModel.enviando.value shouldBe false
        // Primero el PUT del estado y después el POST de la acción
        coVerifyOrder {
            repo.cambiarEstado("EV-0001", EstadoAtencion.EN_ATENCION)
            repo.registrarAccion("EV-0001", OPERADOR_DE_PRUEBA, "Tomó el evento")
        }
    }

    "marcar como atendido cierra el evento, guarda la nota en el historial y limpia el campo" {
        val repo = repoConEvento()
        val descripcion = "Marcó como atendido: Falsa alarma"
        val accion = accionDePrueba(2, OPERADOR_DE_PRUEBA, descripcion, "2026-11-09T11:05:00")
        coEvery { repo.cambiarEstado("EV-0001", EstadoAtencion.ATENDIDA) } returns
            pendiente.copy(estado = EstadoAtencion.ATENDIDA)
        coEvery { repo.registrarAccion("EV-0001", OPERADOR_DE_PRUEBA, descripcion) } returns accion
        val viewModel = DetalleEventoViewModel(repo)
        viewModel.cargar("EV-0001")
        viewModel.onNotaChange("  Falsa alarma ")

        viewModel.marcarAtendido(OPERADOR_DE_PRUEBA)

        val evento = (viewModel.estado.value as UiState.Exito).datos
        evento.estado shouldBe EstadoAtencion.ATENDIDA
        evento.acciones.last().descripcion shouldBe descripcion
        viewModel.nota.value shouldBe ""
        viewModel.errorAccion.value shouldBe null
    }

    "escalar deja constancia en el historial sin cambiar el estado" {
        val repo = repoConEvento()
        val accion = accionDePrueba(2, OPERADOR_DE_PRUEBA, "Escaló el evento", "2026-11-09T10:55:00")
        coEvery { repo.registrarAccion("EV-0001", OPERADOR_DE_PRUEBA, "Escaló el evento") } returns accion
        val viewModel = DetalleEventoViewModel(repo)
        viewModel.cargar("EV-0001")

        viewModel.escalar(OPERADOR_DE_PRUEBA)

        val evento = (viewModel.estado.value as UiState.Exito).datos
        evento.estado shouldBe EstadoAtencion.PENDIENTE
        evento.acciones.last() shouldBe accion
        coVerify(exactly = 0) { repo.cambiarEstado(any(), any()) }
    }

    "si falla el cambio de estado el evento no cambia y se avisa el error" {
        val repo = repoConEvento()
        coEvery { repo.cambiarEstado(any(), any()) } throws IOException("sin red")
        val viewModel = DetalleEventoViewModel(repo)
        viewModel.cargar("EV-0001")

        viewModel.marcarAtendido(OPERADOR_DE_PRUEBA)

        viewModel.estado.value shouldBe UiState.Exito(pendiente)
        viewModel.errorAccion.value shouldBe "Sin conexión. Revisa tu red e inténtalo de nuevo."
        viewModel.enviando.value shouldBe false
        coVerify(exactly = 0) { repo.registrarAccion(any(), any(), any()) }

        viewModel.descartarErrorAccion()
        viewModel.errorAccion.value shouldBe null
    }

    "un evento ya atendido no acepta más acciones" {
        val repo = mockk<EventoRepository>()
        coEvery { repo.obtenerEvento("EV-0009") } returns
            eventoDePrueba(id = "EV-0009", estado = EstadoAtencion.ATENDIDA)
        val viewModel = DetalleEventoViewModel(repo)
        viewModel.cargar("EV-0009")

        viewModel.tomarEvento(OPERADOR_DE_PRUEBA)
        viewModel.escalar(OPERADOR_DE_PRUEBA)
        viewModel.marcarAtendido(OPERADOR_DE_PRUEBA)

        coVerify(exactly = 0) { repo.cambiarEstado(any(), any()) }
        coVerify(exactly = 0) { repo.registrarAccion(any(), any(), any()) }
    }
})
