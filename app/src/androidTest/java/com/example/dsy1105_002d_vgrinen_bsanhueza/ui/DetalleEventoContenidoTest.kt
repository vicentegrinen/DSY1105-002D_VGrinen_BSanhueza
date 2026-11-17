package com.example.dsy1105_002d_vgrinen_bsanhueza.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.DetalleEventoContenido
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.DSY1105002D_VGrinen_BSanhuezaTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// Guía 15: pruebas de interfaz del detalle del evento (Compose UI Test + JUnit4),
// centradas en el botón "Marcar como atendido".
class DetalleEventoContenidoTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun mostrar(
        evento: Evento,
        enviando: Boolean = false,
        errorAccion: String? = null,
        puedeRegistrarAcciones: Boolean = true,
        onMarcarAtendido: () -> Unit = {}
    ) {
        composeRule.setContent {
            DSY1105002D_VGrinen_BSanhuezaTheme(darkTheme = false) {
                DetalleEventoContenido(
                    estado = UiState.Exito(evento),
                    nota = "",
                    enviando = enviando,
                    errorAccion = errorAccion,
                    onNotaChange = {},
                    onTomar = {},
                    onEscalar = {},
                    onMarcarAtendido = onMarcarAtendido,
                    onDescartarError = {},
                    onReintentar = {},
                    onVolver = {},
                    puedeRegistrarAcciones = puedeRegistrarAcciones
                )
            }
        }
    }

    @Test
    fun elBotonMarcarComoAtendidoSeVeYAvisaAlTocarlo() {
        var veces = 0
        mostrar(eventoDePruebaUi(), onMarcarAtendido = { veces++ })

        composeRule.onNodeWithText("Marcar como atendido")
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()

        assertEquals(1, veces)
    }

    @Test
    fun enUnEventoYaAtendidoElBotonQuedaDeshabilitado() {
        mostrar(eventoDePruebaUi(estado = EstadoAtencion.ATENDIDA))

        composeRule.onNodeWithText("Estado: Atendida").assertIsDisplayed()
        composeRule.onNodeWithText("Marcar como atendido")
            .performScrollTo()
            .assertIsNotEnabled()
    }

    @Test
    fun mientrasSeEnviaUnaAccionElBotonQuedaDeshabilitado() {
        mostrar(eventoDePruebaUi(), enviando = true)

        composeRule.onNodeWithText("Marcar como atendido")
            .performScrollTo()
            .assertIsNotEnabled()
    }

    @Test
    fun elRolDeSeguimientoNoVeElBoton() {
        mostrar(eventoDePruebaUi(), puedeRegistrarAcciones = false)

        composeRule.onNodeWithText("Marcar como atendido").assertDoesNotExist()
        composeRule.onNodeWithText("Tomar evento").assertDoesNotExist()
        composeRule.onNodeWithText("Historial de acciones").assertIsDisplayed()
    }

    @Test
    fun siFallaUnaAccionSeVeElBannerDeError() {
        mostrar(
            eventoDePruebaUi(),
            errorAccion = "Sin conexión. Revisa tu red e inténtalo de nuevo."
        )

        composeRule
            .onNodeWithText("Sin conexión. Revisa tu red e inténtalo de nuevo.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Entendido").assertIsDisplayed()
    }
}
