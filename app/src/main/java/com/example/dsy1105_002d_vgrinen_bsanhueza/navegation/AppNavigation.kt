package com.example.dsy1105_002d_vgrinen_bsanhueza.navegation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.LoginUiState
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.Seccion
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.puedeRegistrarAcciones
import com.example.dsy1105_002d_vgrinen_bsanhueza.model.secciones
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.CamaraRepository
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.CuentaRepository
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.EventoRepository
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.UsuarioPruebaRepository
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.BarraInferiorGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components.PestanaGuardian
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.BienvenidaScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.CamarasScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.DetalleEventoScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.EventosScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.ListadoOfflineScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.LoginScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.PanelScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.PantallaPrincipal
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.PerfilGuardianScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.PerfilScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.RegistroCuentaScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.RegistroIntegralScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.ResumenScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens.UsuariosScreen
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.CamarasViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.CamarasViewModelFactory
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.DetalleEventoViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.DetalleEventoViewModelFactory
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.EventosViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.EventosViewModelFactory
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.LoginViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.LoginViewModelFactory
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.PanelViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.PanelViewModelFactory
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.RegistroCuentaViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.RegistroCuentaViewModelFactory
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.UsuarioViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.UsuariosViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.UsuariosViewModelFactory

@Composable
fun AppNavigation(
    usuarioViewModel: UsuarioViewModel,
    cuentaRepository: CuentaRepository,
    eventoRepository: EventoRepository,
    camaraRepository: CamaraRepository,
    usuarioPruebaRepository: UsuarioPruebaRepository
) {
    val navController = rememberNavController()

    // Los ViewModels se crean una sola vez y se comparten entre pantallas
    val loginViewModel: LoginViewModel =
        viewModel(factory = LoginViewModelFactory(cuentaRepository))
    val registroCuentaViewModel: RegistroCuentaViewModel =
        viewModel(factory = RegistroCuentaViewModelFactory(cuentaRepository))

    // Modo Guardián: al vivir aquí conservan los últimos datos cargados al cambiar de pestaña
    val panelViewModel: PanelViewModel =
        viewModel(factory = PanelViewModelFactory(eventoRepository))
    val eventosViewModel: EventosViewModel =
        viewModel(factory = EventosViewModelFactory(eventoRepository))
    val camarasViewModel: CamarasViewModel =
        viewModel(factory = CamarasViewModelFactory(camaraRepository))
    val usuariosViewModel: UsuariosViewModel =
        viewModel(factory = UsuariosViewModelFactory(usuarioPruebaRepository))

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { entry ->
            // "cuenta_creada" lo activa RegistroCuentaScreen al terminar el registro
            val cuentaCreada by entry.savedStateHandle
                .getStateFlow("cuenta_creada", false)
                .collectAsState()
            LoginScreen(
                navController = navController,
                viewModel = loginViewModel,
                cuentaCreada = cuentaCreada,
                onMensajeVisto = { entry.savedStateHandle["cuenta_creada"] = false }
            )
        }
        // Registro de usuarios nuevos (se llega desde el login)
        composable("crear_cuenta") {
            RegistroCuentaScreen(
                navController = navController,
                viewModel = registroCuentaViewModel,
                onSocialExito = { resultado ->
                    loginViewModel.iniciarSesionSocial(resultado.correo)
                    navController.navigate(Rutas.inicioPara(loginViewModel.estado.value.rol)) {
                        // Quita login y registro de la pila
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        // Pantalla anterior al Panel de Modo Guardián: se conserva, pero el login ya no navega a ella
        composable("bienvenida") {
            BienvenidaScreen(navController, loginViewModel)
        }
        // Pantalla de la Guía 12 (estado + DataStore + animaciones)
        composable("modo_especial") {
            PantallaPrincipal()
        }
        // Pantalla de la Guía 13 (cámara y galería)
        composable("perfil") {
            PerfilScreen()
        }
        // Guía 14: módulo integral (UsuarioViewModel compartido entre las 3 pantallas)
        composable("registro") {
            RegistroIntegralScreen(navController, usuarioViewModel)
        }
        composable("resumen") {
            ResumenScreen(navController, usuarioViewModel)
        }
        composable("listado") {
            ListadoOfflineScreen(usuarioViewModel)
        }

        // ---- Modo Guardián: pantallas con barra inferior ----
        // Sin animación entre pestañas para que la barra inferior no parpadee
        composable(
            Rutas.PANEL,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            PantallaConBarra(Seccion.PANEL, navController, loginViewModel) { sesion ->
                PanelScreen(
                    viewModel = panelViewModel,
                    correo = sesion.correo.trim(),
                    rol = sesion.rol,
                    onAbrirEvento = { id -> navController.navigate(Rutas.detalleEvento(id)) }
                )
            }
        }
        composable(
            Rutas.EVENTOS,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            PantallaConBarra(Seccion.EVENTOS, navController, loginViewModel) {
                EventosScreen(
                    viewModel = eventosViewModel,
                    onAbrirEvento = { id -> navController.navigate(Rutas.detalleEvento(id)) }
                )
            }
        }
        composable(
            Rutas.DETALLE_EVENTO,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { entry ->
            val eventoId = entry.arguments?.getString(Rutas.ARG_EVENTO_ID).orEmpty()
            // Este ViewModel vive solo mientras el detalle está abierto
            val detalleViewModel: DetalleEventoViewModel =
                viewModel(factory = DetalleEventoViewModelFactory(eventoRepository))
            PantallaConBarra(Seccion.EVENTOS, navController, loginViewModel) { sesion ->
                DetalleEventoScreen(
                    viewModel = detalleViewModel,
                    eventoId = eventoId,
                    usuario = sesion.correo.trim(),
                    // Solo el Operador registra acciones; el Supervisor ve el evento en modo seguimiento
                    puedeRegistrarAcciones = sesion.rol?.puedeRegistrarAcciones == true,
                    onVolver = { navController.popBackStack() }
                )
            }
        }
        composable(
            Rutas.CAMARAS,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            PantallaConBarra(Seccion.CAMARAS, navController, loginViewModel) {
                CamarasScreen(viewModel = camarasViewModel)
            }
        }
        // Administración de usuarios de prueba: solo la ve el rol Admin
        composable(
            Rutas.USUARIOS,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            PantallaConBarra(Seccion.USUARIOS, navController, loginViewModel) { sesion ->
                UsuariosScreen(
                    viewModel = usuariosViewModel,
                    correoSesion = sesion.correo.trim()
                )
            }
        }
        composable(
            Rutas.MI_PERFIL,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            PantallaConBarra(Seccion.PERFIL, navController, loginViewModel) { sesion ->
                PerfilGuardianScreen(
                    correo = sesion.correo.trim(),
                    rol = sesion.rol,
                    onAbrirModulo = { ruta -> navController.navigate(ruta) },
                    // Al quedar sin sesión, PantallaConBarra vuelve al login
                    onCerrarSesion = { loginViewModel.cerrarSesion() }
                )
            }
        }
    }
}

// Pestaña de la barra inferior que corresponde a cada sección
private fun pestanaDe(seccion: Seccion): PestanaGuardian = when (seccion) {
    Seccion.PANEL -> PestanaGuardian(Rutas.PANEL, "Panel", Icons.Filled.Dashboard)
    Seccion.EVENTOS -> PestanaGuardian(Rutas.EVENTOS, "Eventos", Icons.Filled.NotificationsActive)
    Seccion.CAMARAS -> PestanaGuardian(Rutas.CAMARAS, "Cámaras", Icons.Filled.Videocam)
    Seccion.USUARIOS -> PestanaGuardian(Rutas.USUARIOS, "Usuarios", Icons.Filled.Group)
    Seccion.PERFIL -> PestanaGuardian(Rutas.MI_PERFIL, "Perfil", Icons.Filled.Person)
}

// Envuelve una pantalla de Modo Guardián: contenido arriba y barra inferior abajo.
// La barra muestra solo las secciones del rol de la sesión; "seccion" es la que se marca activa.
// Le entrega al contenido la sesión actual.
@Composable
private fun PantallaConBarra(
    seccion: Seccion,
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    contenido: @Composable (sesion: LoginUiState) -> Unit
) {
    val sesion by loginViewModel.estado.collectAsState()
    val rol = sesion.rol

    // Sin sesión (se cerró o el sistema reinició la app): vuelve al login y limpia la pila
    if (rol == null) {
        LaunchedEffect(Unit) {
            navController.navigate(Rutas.LOGIN) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
        return
    }

    val secciones = rol.secciones()
    val inicio = Rutas.inicioPara(rol)

    // El rol no tiene permitida esta sección: se le lleva a su pantalla de inicio
    if (seccion !in secciones) {
        LaunchedEffect(Unit) {
            navController.navigate(inicio) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            contenido(sesion)
        }
        BarraInferiorGuardian(
            pestanas = secciones.map { pestanaDe(it) },
            rutaSeleccionada = Rutas.de(seccion),
            onSeleccionar = { ruta ->
                navController.navigate(ruta) {
                    // La pantalla de inicio del rol queda como base: "atrás" vuelve a ella
                    popUpTo(inicio)
                    launchSingleTop = true
                }
            }
        )
    }
}
