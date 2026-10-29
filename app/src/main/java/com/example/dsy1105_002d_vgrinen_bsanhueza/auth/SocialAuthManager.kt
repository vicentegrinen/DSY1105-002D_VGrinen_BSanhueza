package com.example.dsy1105_002d_vgrinen_bsanhueza.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.dsy1105_002d_vgrinen_bsanhueza.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthWebException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

// Resultado de un intento de login con proveedor externo
sealed interface ResultadoSocial {
    data class Exito(
        val correo: String,
        val nombre: String?,
        val proveedor: String
    ) : ResultadoSocial

    // El usuario cerró el selector de cuentas: no es un error, no se muestra mensaje
    data object Cancelado : ResultadoSocial

    data class Error(val mensaje: String) : ResultadoSocial
}

// Login con Google (Credential Manager) y con Apple (flujo web de Firebase), ambos
// terminan en un FirebaseUser. Sirve igual para "iniciar sesión" y para "registrarse":
// si la cuenta no existe, Firebase la crea en el primer ingreso.
class SocialAuthManager(context: Context) {

    private companion object {
        const val TAG = "SocialAuth"
    }

    private val appContext = context.applicationContext
    private val auth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(appContext)

    suspend fun conGoogle(activity: Activity): ResultadoSocial {
        return try {
            // default_web_client_id lo genera el plugin google-services desde google-services.json
            val opcion = GetSignInWithGoogleOption.Builder(
                appContext.getString(R.string.default_web_client_id)
            ).build()
            val solicitud = GetCredentialRequest.Builder()
                .addCredentialOption(opcion)
                .build()

            val credencial = credentialManager.getCredential(activity, solicitud).credential
            if (credencial !is CustomCredential ||
                credencial.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return ResultadoSocial.Error("Google devolvió un tipo de credencial inesperado")
            }

            val token = GoogleIdTokenCredential.createFrom(credencial.data).idToken
            val resultado = auth
                .signInWithCredential(GoogleAuthProvider.getCredential(token, null))
                .await()
            aResultado(resultado.user, "Google")
        } catch (e: CancellationException) {
            throw e
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Google: el usuario cerró el selector de cuentas")
            ResultadoSocial.Cancelado
        } catch (e: NoCredentialException) {
            fallo("No hay una cuenta de Google en este dispositivo. Agrega una en Ajustes.", e)
        } catch (e: GetCredentialException) {
            fallo("Google rechazó la solicitud (revisa SHA-1 y Web client ID).", e)
        } catch (e: GoogleIdTokenParsingException) {
            fallo("Respuesta de Google inválida.", e)
        } catch (e: FirebaseNetworkException) {
            fallo("Sin conexión a internet", e)
        } catch (e: FirebaseAuthException) {
            fallo("Firebase rechazó la credencial (${e.errorCode}).", e)
        } catch (e: Exception) {
            fallo("Error inesperado con Google.", e)
        }
    }

    suspend fun conApple(activity: Activity): ResultadoSocial {
        return try {
            val proveedor = OAuthProvider.newBuilder("apple.com")
                .setScopes(listOf("email", "name"))
                .build()

            // Si el sistema mató la app mientras se mostraba Apple, se retoma ese resultado
            val pendiente = auth.pendingAuthResult
            val resultado = (pendiente ?: auth.startActivityForSignInWithProvider(activity, proveedor))
                .await()
            aResultado(resultado.user, "Apple")
        } catch (e: CancellationException) {
            throw e
        } catch (e: FirebaseAuthWebException) {
            if (e.errorCode == "ERROR_WEB_CONTEXT_CANCELED") {
                Log.d(TAG, "Apple: el usuario cerró la ventana")
                ResultadoSocial.Cancelado
            } else {
                fallo("No se pudo completar el inicio con Apple (${e.errorCode}).", e)
            }
        } catch (e: FirebaseNetworkException) {
            fallo("Sin conexión a internet", e)
        } catch (e: FirebaseAuthException) {
            fallo("Firebase rechazó el inicio con Apple (${e.errorCode}).", e)
        } catch (e: Exception) {
            fallo("Error inesperado con Apple.", e)
        }
    }

    fun cerrarSesion() {
        runCatching { auth.signOut() }
    }

    // Registra el error completo en Logcat (filtra por la etiqueta "SocialAuth") y muestra
    // el tipo de excepción en pantalla para poder diagnosticar. Cuando todo funcione, puedes
    // quitar la segunda línea del mensaje.
    private fun fallo(mensaje: String, e: Throwable): ResultadoSocial.Error {
        Log.e(TAG, mensaje, e)
        return ResultadoSocial.Error(
            "$mensaje\n(${e.javaClass.simpleName}: ${e.localizedMessage ?: "sin detalle"})"
        )
    }

    private fun aResultado(user: FirebaseUser?, proveedor: String): ResultadoSocial {
        // Apple puede entregar un correo de retransmisión privado; sigue siendo un correo válido
        val correo = user?.email?.takeIf { it.isNotBlank() }
            ?: user?.providerData?.firstNotNullOfOrNull { it.email?.takeIf { c -> c.isNotBlank() } }
        return if (user == null || correo == null) {
            ResultadoSocial.Error("La cuenta no entregó un correo electrónico")
        } else {
            ResultadoSocial.Exito(correo = correo, nombre = user.displayName, proveedor = proveedor)
        }
    }
}
