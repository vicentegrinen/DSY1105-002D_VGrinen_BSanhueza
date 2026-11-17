package com.example.dsy1105_002d_vgrinen_bsanhueza.remote

import com.example.dsy1105_002d_vgrinen_bsanhueza.model.AccionEvento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Camara
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.EstadoAtencion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Evento
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Prioridad
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.UsuarioPrueba
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DatosSimulados {

    private const val USUARIO_SISTEMA = AccionEvento.USUARIO_SISTEMA
    private const val OPERADOR = "operador@guardian.test"
    private const val SUPERVISOR = "supervisor@guardian.test"

    // Formato ISO local con segundos: "2026-11-09T10:42:00"
    fun formatear(fechaHora: LocalDateTime): String =
        fechaHora.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    fun eventos(ahora: LocalDateTime): List<Evento> {
        var idAccion = 0
        fun hace(minutos: Long) = formatear(ahora.minusMinutes(minutos))
        fun accion(usuario: String, descripcion: String, haceMinutos: Long) =
            AccionEvento(++idAccion, usuario, descripcion, hace(haceMinutos))
        fun alerta(haceMinutos: Long) =
            accion(USUARIO_SISTEMA, "Alerta generada por el sistema", haceMinutos)

        return listOf(
            Evento(
                id = "EV-0412",
                titulo = "Movimiento fuera de horario",
                camaraId = "Cam-03",
                camaraNombre = "Bodega norte",
                ubicacionReferencial = "Piso 1, sector norte",
                prioridad = Prioridad.CRITICA,
                fechaHora = hace(18),
                estado = EstadoAtencion.PENDIENTE,
                acciones = listOf(alerta(18))
            ),
            Evento(
                id = "EV-0411",
                titulo = "Cámara sin señal",
                camaraId = "Cam-07",
                camaraNombre = "Acceso principal",
                ubicacionReferencial = "Entrada principal, nivel calle",
                prioridad = Prioridad.CRITICA,
                fechaHora = hace(45),
                estado = EstadoAtencion.EN_ATENCION,
                acciones = listOf(
                    alerta(45),
                    accion(OPERADOR, "Tomó el evento", 38)
                )
            ),
            Evento(
                id = "EV-0410",
                titulo = "Puerta abierta",
                camaraId = "Cam-01",
                camaraNombre = "Pasillo B",
                ubicacionReferencial = "Piso 2, ala oriente",
                prioridad = Prioridad.MEDIA,
                fechaHora = hace(62),
                estado = EstadoAtencion.PENDIENTE,
                acciones = listOf(alerta(62))
            ),
            Evento(
                id = "EV-0409",
                titulo = "Cambio de iluminación",
                camaraId = "Cam-05",
                camaraNombre = "Estacionamiento",
                ubicacionReferencial = "Subterráneo -1",
                prioridad = Prioridad.BAJA,
                fechaHora = hace(100),
                estado = EstadoAtencion.ATENDIDA,
                acciones = listOf(
                    alerta(100),
                    accion(OPERADOR, "Tomó el evento", 96),
                    accion(OPERADOR, "Marcó como atendido: luminaria en mantención.", 93)
                )
            ),
            Evento(
                id = "EV-0408",
                titulo = "Persona en zona restringida",
                camaraId = "Cam-02",
                camaraNombre = "Sala de servidores",
                ubicacionReferencial = "Piso 3, acceso controlado",
                prioridad = Prioridad.CRITICA,
                fechaHora = hace(135),
                estado = EstadoAtencion.ATENDIDA,
                acciones = listOf(
                    alerta(135),
                    accion(OPERADOR, "Tomó el evento", 131),
                    accion(SUPERVISOR, "Escaló el evento a jefatura de turno", 130),
                    accion(OPERADOR, "Marcó como atendido: técnico autorizado, credencial validada.", 127)
                )
            ),
            Evento(
                id = "EV-0407",
                titulo = "Objeto abandonado",
                camaraId = "Cam-04",
                camaraNombre = "Hall central",
                ubicacionReferencial = "Piso 1, recepción",
                prioridad = Prioridad.MEDIA,
                fechaHora = hace(170),
                estado = EstadoAtencion.ATENDIDA,
                acciones = listOf(
                    alerta(170),
                    accion(OPERADOR, "Tomó el evento", 166),
                    accion(OPERADOR, "Marcó como atendido: se retira caja vacía.", 164)
                )
            ),
            Evento(
                id = "EV-0406",
                titulo = "Vibración en reja perimetral",
                camaraId = "Cam-06",
                camaraNombre = "Perímetro sur",
                ubicacionReferencial = "Exterior, cierre sur",
                prioridad = Prioridad.BAJA,
                fechaHora = hace(200),
                estado = EstadoAtencion.PENDIENTE,
                acciones = listOf(alerta(200))
            ),
            Evento(
                id = "EV-0405",
                titulo = "Acceso fuera de turno",
                camaraId = "Cam-07",
                camaraNombre = "Acceso principal",
                ubicacionReferencial = "Entrada principal, nivel calle",
                prioridad = Prioridad.MEDIA,
                fechaHora = hace(240),
                estado = EstadoAtencion.ATENDIDA,
                acciones = listOf(
                    alerta(240),
                    accion(OPERADOR, "Tomó el evento", 233),
                    accion(OPERADOR, "Marcó como atendido: ingreso programado de aseo.", 230)
                )
            )
        )
    }

    // Los mismos tres usuarios ficticios que pueden ingresar a la app (sin contraseñas)
    fun usuarios(): List<UsuarioPrueba> = listOf(
        UsuarioPrueba(1, "Admin Demo", "admin@guardian.test", Rol.ADMIN),
        UsuarioPrueba(2, "Supervisor Demo", SUPERVISOR, Rol.SUPERVISOR),
        UsuarioPrueba(3, "Operador Demo", OPERADOR, Rol.OPERADOR)
    )

    // alertasPendientes parte en 0: ServidorSimulado lo calcula con los eventos vigentes
    fun camaras(ahora: LocalDateTime): List<Camara> = listOf(
        Camara("Cam-01", "Pasillo B", enLinea = true, ultimaSenal = null, alertasPendientes = 0),
        Camara("Cam-02", "Sala de servidores", enLinea = true, ultimaSenal = null, alertasPendientes = 0),
        Camara("Cam-03", "Bodega norte", enLinea = true, ultimaSenal = null, alertasPendientes = 0),
        Camara("Cam-04", "Hall central", enLinea = true, ultimaSenal = null, alertasPendientes = 0),
        Camara("Cam-05", "Estacionamiento", enLinea = true, ultimaSenal = null, alertasPendientes = 0),
        Camara("Cam-06", "Perímetro sur", enLinea = true, ultimaSenal = null, alertasPendientes = 0),
        Camara(
            "Cam-07", "Acceso principal",
            enLinea = false,
            ultimaSenal = formatear(ahora.minusMinutes(45)),
            alertasPendientes = 0
        )
    )
}
