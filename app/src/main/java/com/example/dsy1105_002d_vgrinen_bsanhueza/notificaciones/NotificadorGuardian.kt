package com.example.dsy1105_002d_vgrinen_bsanhueza.notificaciones

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.dsy1105_002d_vgrinen_bsanhueza.MainActivity
import com.example.dsy1105_002d_vgrinen_bsanhueza.R

// Notificación de prueba de Modo Guardián. El contenido es ficticio:
// no viene de ningún evento real ni se envía a ningún servidor.
object NotificadorGuardian {

    private const val CANAL_ID = "alertas_guardian"
    private const val ID_NOTIFICACION_PRUEBA = 1001

    // Desde Android 13 las notificaciones necesitan permiso en tiempo de ejecución
    fun tienePermiso(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    // Muestra la notificación. Devuelve false si falta el permiso.
    @SuppressLint("MissingPermission") // el permiso se revisa en la primera línea
    fun enviarPrueba(context: Context): Boolean {
        if (!tienePermiso(context)) return false
        crearCanal(context)

        // Al tocar la notificación se abre (o se trae al frente) la app
        val abrirApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion_guardian)
            .setContentTitle("Modo Guardián: alerta de prueba")
            .setContentText("Movimiento fuera de horario · Cam-03 (dato ficticio)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(ID_NOTIFICACION_PRUEBA, notificacion)
        return true
    }

    // El canal es obligatorio desde Android 8; crearlo de nuevo no tiene efecto si ya existe
    private fun crearCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL_ID,
            "Alertas de Modo Guardián",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alertas de eventos de seguridad (datos de prueba)"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }
}
