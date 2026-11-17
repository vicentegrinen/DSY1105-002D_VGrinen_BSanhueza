package com.example.dsy1105_002d_vgrinen_bsanhueza.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.FiltroEventos
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Prioridad
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.EventosContenido
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.DSY1105002D_VGrinen_BSanhuezaTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// Guía 15: pruebas de interfaz de la lista de eventos (Compose UI Test + JUnit4).
// Se dibuja EventosContenido, que no depende del ViewModel: recibe el estado ya armado.
class EventosContenidoTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val eventos = listOf(
        eventoDePruebaUi(id = "EV-0003", titulo = "Puerta forzada"),
        eventoDePruebaUi(
            id = "EV-0002",
            titulo = "Cámara obstruida",
            prioridad = Prioridad.MEDIA,
            estado = EstadoAtencion.EN_ATENCION
        ),
        eventoDePruebaUi(
            id = "EV-0001",
            titulo = "Ronda sin novedad",
            prioridad = Prioridad.BAJA,
            estado = EstadoAtencion.ATENDIDA
        )
    )

    private fun mostrar(
        estado: UiState<List<Evento>>,
        onReintentar: () -> Unit = {},
        onAbrirEvento: (String) -> Unit = {}
    ) {
        composeRule.setContent {
            DSY1105002D_VGrinen_BSanhuezaTheme(darkTheme = false) {
                EventosContenido(
                    estado = estado,
                    filtro = FiltroEventos.TODOS,
                    onFiltro = {},
                    onReintentar = onReintentar,
                    onAbrirEvento = onAbrirEvento
                )
            }
        }
    }

    @Test
    fun laListaMuestraElTituloDeCadaEvento() {
        mostrar(UiState.Exito(eventos))

        composeRule.onNodeWithText("Puerta forzada").assertIsDisplayed()
        composeRule.onNodeWithText("Cámara obstruida").assertIsDisplayed()
        composeRule.onNodeWithText("Ronda sin novedad").assertIsDisplayed()
        composeRule.onNodeWithText("3 eventos").assertIsDisplayed()
    }

    @Test
    fun alTocarUnEventoSeAbreSuDetalle() {
        var abierto: String? = null
        mostrar(UiState.Exito(eventos), onAbrirEvento = { abierto = it })

        composeRule.onNodeWithText("Cámara obstruida").performClick()

        assertEquals("EV-0002", abierto)
    }

    @Test
    fun sinConexionYSinDatosSeVeElBannerDeErrorConReintentar() {
        var reintentos = 0
        mostrar(
            UiState.Error("Sin conexión. Revisa tu red e inténtalo de nuevo."),
            onReintentar = { reintentos++ }
        )

        composeRule
            .onNodeWithText("Sin conexión. Revisa tu red e inténtalo de nuevo.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Reintentar").performClick()

        assertEquals(1, reintentos)
    }

    @Test
    fun sinConexionConDatosGuardadosSeVeElBannerYLaLista() {
        mostrar(UiState.Error("Sin conexión. Mostrando datos guardados.", eventos))

        composeRule.onNodeWithText("Sin conexión. Mostrando datos guardados.").assertIsDisplayed()
        composeRule.onNodeWithText("Reintentar").assertIsDisplayed()
        composeRule.onNodeWithText("Puerta forzada").assertIsDisplayed()
    }

    @Test
    fun unaListaVaciaAvisaQueNoHayEventos() {
        mostrar(UiState.Exito(emptyList()))

        composeRule.onNodeWithText("No hay eventos para este filtro.").assertIsDisplayed()
    }
}
