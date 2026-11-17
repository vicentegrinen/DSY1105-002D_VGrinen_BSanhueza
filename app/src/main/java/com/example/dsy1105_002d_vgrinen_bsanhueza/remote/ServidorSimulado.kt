package com.example.dsy1105_002d_vgrinen_bsanhueza.remote

import com.example.dsy1105_002d_vgrinen_bsanhueza.model.AccionEvento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Camara
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Indicadores
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UsuarioPrueba
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt

// Respuesta HTTP mínima: código y cuerpo en JSON.
data class RespuestaSimulada(val codigo: Int, val json: String)

// Backend de prueba en memoria. Atiende los mismos endpoints que declara ApiService
// y guarda los cambios (POST y PUT) mientras la app esté abierta.
// "ahora" se recibe por parámetro para poder fijar la hora en las pruebas.
class ServidorSimulado(
    private val ahora: () -> LocalDateTime = { LocalDateTime.now() }
) {
    private val gson = Gson()
    private val eventos: MutableList<Evento> = DatosSimulados.eventos(ahora()).toMutableList()
    private val camaras: List<Camara> = DatosSimulados.camaras(ahora())
    private val usuarios: MutableList<UsuarioPrueba> = DatosSimulados.usuarios().toMutableList()
    private var ultimoIdAccion: Int = eventos.flatMap { it.acciones }.maxOfOrNull { it.id } ?: 0

    // Recibe método, segmentos de la ruta (por ejemplo ["eventos", "EV-0412", "estado"]) y cuerpo JSON
    @Synchronized
    fun responder(metodo: String, ruta: List<String>, cuerpo: String?): RespuestaSimulada {
        val segmentos = ruta.filter { it.isNotEmpty() }
        val esRutaDeEvento = segmentos.size == 3 && segmentos[0] == "eventos"

        return when {
            metodo == "GET" && segmentos == listOf("eventos") ->
                ok(eventos.sortedByDescending { it.fechaHora })

            metodo == "GET" && segmentos.size == 2 && segmentos[0] == "eventos" ->
                buscar(segmentos[1])?.let { ok(it) } ?: noEncontrado()

            metodo == "POST" && esRutaDeEvento && segmentos[2] == "acciones" ->
                registrarAccion(segmentos[1], cuerpo)

            metodo == "PUT" && esRutaDeEvento && segmentos[2] == "estado" ->
                cambiarEstado(segmentos[1], cuerpo)

            metodo == "GET" && segmentos == listOf("camaras") ->
                ok(camarasConAlertas())

            metodo == "GET" && segmentos == listOf("indicadores") ->
                ok(calcularIndicadores(eventos, ahora().toLocalDate()))

            metodo == "GET" && segmentos == listOf("usuarios") ->
                ok(usuarios)

            metodo == "PUT" && segmentos.size == 3 &&
                segmentos[0] == "usuarios" && segmentos[2] == "rol" ->
                cambiarRol(segmentos[1], cuerpo)

            else -> noEncontrado()
        }
    }

    private fun registrarAccion(eventoId: String, cuerpo: String?): RespuestaSimulada {
        val evento = buscar(eventoId) ?: return noEncontrado()
        val solicitud = leer(cuerpo, NuevaAccionRequest::class.java)
        val usuario = solicitud?.usuario
        val descripcion = solicitud?.descripcion
        if (usuario.isNullOrBlank() || descripcion.isNullOrBlank()) {
            return solicitudInvalida("La acción necesita usuario y descripción")
        }

        val accion = AccionEvento(
            id = ++ultimoIdAccion,
            usuario = usuario.trim(),
            descripcion = descripcion.trim(),
            fechaHora = DatosSimulados.formatear(ahora())
        )
        reemplazar(evento.copy(acciones = evento.acciones + accion))
        return RespuestaSimulada(201, gson.toJson(accion))
    }

    private fun cambiarEstado(eventoId: String, cuerpo: String?): RespuestaSimulada {
        val evento = buscar(eventoId) ?: return noEncontrado()
        val nuevoEstado = leer(cuerpo, CambioEstadoRequest::class.java)?.estado
            ?: return solicitudInvalida("Estado de atención no válido")

        val actualizado = evento.copy(estado = nuevoEstado)
        reemplazar(actualizado)
        return ok(actualizado)
    }

    // El cambio vive solo en memoria: no modifica las credenciales de prueba del login
    private fun cambiarRol(idTexto: String, cuerpo: String?): RespuestaSimulada {
        val indice = usuarios.indexOfFirst { it.id == idTexto.toIntOrNull() }
        if (indice < 0) return noEncontrado()
        val nuevoRol = leer(cuerpo, CambioRolRequest::class.java)?.rol
            ?: return solicitudInvalida("Rol no válido")

        val actualizado = usuarios[indice].copy(rol = nuevoRol)
        usuarios[indice] = actualizado
        return ok(actualizado)
    }

    // Cada cámara informa cuántos de sus eventos siguen sin atender
    private fun camarasConAlertas(): List<Camara> = camaras.map { camara ->
        camara.copy(
            alertasPendientes = eventos.count {
                it.camaraId == camara.id && it.estado != EstadoAtencion.ATENDIDA
            }
        )
    }

    private fun buscar(id: String): Evento? = eventos.firstOrNull { it.id == id }

    private fun reemplazar(evento: Evento) {
        val indice = eventos.indexOfFirst { it.id == evento.id }
        if (indice >= 0) eventos[indice] = evento
    }

    // Devuelve null si el cuerpo viene vacío o no es JSON válido
    private fun <T> leer(cuerpo: String?, tipo: Class<T>): T? =
        try {
            gson.fromJson(cuerpo, tipo)
        } catch (e: JsonSyntaxException) {
            null
        }

    private fun ok(cuerpo: Any) = RespuestaSimulada(200, gson.toJson(cuerpo))

    private fun noEncontrado() = fallo(404, "Recurso no encontrado")

    private fun solicitudInvalida(mensaje: String) = fallo(400, mensaje)

    private fun fallo(codigo: Int, mensaje: String) =
        RespuestaSimulada(codigo, gson.toJson(mapOf("mensaje" to mensaje)))
}

// Indicadores del panel calculados sobre los eventos del día "hoy".
// El tiempo de respuesta de un evento atendido va desde la alerta hasta su última acción.
fun calcularIndicadores(eventos: List<Evento>, hoy: LocalDate): Indicadores {
    val deHoy = eventos.filter { it.fechaHora.startsWith(hoy.toString()) }
    val atendidos = deHoy.filter { it.estado == EstadoAtencion.ATENDIDA }

    val minutos = atendidos.mapNotNull { evento ->
        val cierre = evento.acciones.lastOrNull()?.fechaHora ?: return@mapNotNull null
        Duration.between(
            LocalDateTime.parse(evento.fechaHora),
            LocalDateTime.parse(cierre)
        ).toMinutes()
    }

    return Indicadores(
        eventosHoy = deHoy.size,
        alertasPendientes = deHoy.size - atendidos.size,
        atendidas = atendidos.size,
        minutosRespuestaPromedio = if (minutos.isEmpty()) 0 else minutos.average().roundToInt()
    )
}
