package com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel

import com.example.dsy1105_002d_vgrinen_bsanhueza.data.CuentaEntity
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Rol
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.CuentaRepository
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

// Guía 15: pruebas del login. El repositorio de cuentas llega por constructor,
// así que se reemplaza por un mock de MockK (sin subclases falsas).
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : StringSpec({

    // viewModelScope usa Dispatchers.Main: en las pruebas se cambia por uno que
    // ejecuta las corrutinas de inmediato
    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    // ---- Validaciones ----

    "con los campos vacíos marca ambos como obligatorios y no consulta las cuentas" {
        val cuentaRepo = mockk<CuentaRepository>()
        val viewModel = LoginViewModel(cuentaRepo)
        var ingreso = false

        viewModel.iniciarSesion { ingreso = true }

        viewModel.estado.value.errores.correo shouldBe "Campo obligatorio"
        viewModel.estado.value.errores.clave shouldBe "Campo obligatorio"
        ingreso shouldBe false
        coVerify(exactly = 0) { cuentaRepo.autenticar(any(), any()) }
    }

    "un correo sin formato válido muestra el error del campo" {
        val viewModel = LoginViewModel(mockk())
        viewModel.onCorreoChange("operador-sin-arroba")
        viewModel.onClaveChange("123456")

        viewModel.iniciarSesion { }

        viewModel.estado.value.errores.correo shouldBe "Correo inválido (ej: nombre@dominio.cl)"
        viewModel.estado.value.errores.clave shouldBe null
        viewModel.estado.value.rol shouldBe null
    }

    "al corregir el correo desaparece su error" {
        val viewModel = LoginViewModel(mockk())
        viewModel.onCorreoChange("operador-sin-arroba")
        viewModel.onClaveChange("123456")
        viewModel.iniciarSesion { }

        viewModel.onCorreoChange("operador@guardian.test")

        viewModel.estado.value.errores.correo shouldBe null
    }

    // ---- Credenciales incorrectas ----

    "credenciales incorrectas muestran el mensaje general y no ingresan" {
        val cuentaRepo = mockk<CuentaRepository>()
        coEvery { cuentaRepo.autenticar(any(), any()) } returns null
        val viewModel = LoginViewModel(cuentaRepo)
        var ingreso = false
        viewModel.onCorreoChange("operador@guardian.test")
        viewModel.onClaveChange("clave-equivocada")

        viewModel.iniciarSesion { ingreso = true }

        viewModel.estado.value.errores.general shouldBe "Correo o contraseña incorrectos"
        viewModel.estado.value.rol shouldBe null
        ingreso shouldBe false
    }

    "al volver a escribir se borra el mensaje de credenciales incorrectas" {
        val cuentaRepo = mockk<CuentaRepository>()
        coEvery { cuentaRepo.autenticar(any(), any()) } returns null
        val viewModel = LoginViewModel(cuentaRepo)
        viewModel.onCorreoChange("nadie@guardian.test")
        viewModel.onClaveChange("123456")
        viewModel.iniciarSesion { }

        viewModel.onClaveChange("1234567")

        viewModel.estado.value.errores.general shouldBe null
    }

    // ---- Rol ----

    "cada usuario de prueba ingresa con su rol" {
        val esperados = mapOf(
            "admin@guardian.test" to Rol.ADMIN,
            "supervisor@guardian.test" to Rol.SUPERVISOR,
            "operador@guardian.test" to Rol.OPERADOR
        )

        esperados.forEach { (correo, rol) ->
            val viewModel = LoginViewModel(mockk())
            var ingreso = false
            viewModel.onCorreoChange(correo)
            viewModel.onClaveChange("123456")

            viewModel.iniciarSesion { ingreso = true }

            viewModel.estado.value.rol shouldBe rol
            ingreso shouldBe true
        }
    }

    "el correo se acepta con mayúsculas y espacios alrededor" {
        val viewModel = LoginViewModel(mockk())
        viewModel.onCorreoChange("  SUPERVISOR@guardian.test ")
        viewModel.onClaveChange("123456")

        viewModel.iniciarSesion { }

        viewModel.estado.value.rol shouldBe Rol.SUPERVISOR
    }

    "una cuenta creada en la app ingresa como Operador" {
        val cuentaRepo = mockk<CuentaRepository>()
        coEvery {
            cuentaRepo.autenticar("nueva@correo.cl", "clave-segura-1")
        } returns mockk<CuentaEntity>()
        val viewModel = LoginViewModel(cuentaRepo)
        viewModel.onCorreoChange("nueva@correo.cl")
        viewModel.onClaveChange("clave-segura-1")

        viewModel.iniciarSesion { }

        viewModel.estado.value.rol shouldBe Rol.OPERADOR
    }

    "al ingresar la contraseña se borra del estado" {
        val viewModel = LoginViewModel(mockk())
        viewModel.onCorreoChange("operador@guardian.test")
        viewModel.onClaveChange("123456")

        viewModel.iniciarSesion { }

        viewModel.estado.value.clave shouldBe ""
    }
})
