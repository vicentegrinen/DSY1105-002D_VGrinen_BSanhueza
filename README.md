# DSY1105-002D_VGrinen_BSanhueza

Aplicación Android desarrollada en Kotlin que parte del flujo de inicio de sesión (login) de la rama `main` (formulario reactivo, validaciones en tiempo real, roles y pantalla de bienvenida) y le suma **creación de cuentas**, **persistencia local con ROOM y DataStore**, **cámara y galería** y un **módulo de registro integral**, aplicando una arquitectura limpia y moderna con las herramientas recomendadas por Google.

Sobre esa base se construyó **Modo Guardián**, un MVP académico de monitoreo de eventos y alertas de seguridad que suma **consumo de API REST con Retrofit** (Guía 14) y **pruebas unitarias y de interfaz** (Guía 15).

> **Todos los datos son ficticios o simulados.** La app no usa credenciales, tokens ni datos personales reales, y no se conecta a cámaras ni a sistemas reales.

---

## Modo Guardián (MVP académico)

### Qué ve cada rol

El rol con que se inicia sesión define las pestañas de la barra inferior y lo que se puede hacer. La regla está en un solo archivo: `model/Permisos.kt`.

| Rol           | Pestañas                          | Pantalla de inicio | Sobre un evento                                   |
|---------------|-----------------------------------|--------------------|---------------------------------------------------|
| Administrador | Usuarios, Perfil                  | Usuarios           | No ve eventos                                     |
| Supervisor    | Panel, Eventos, Cámaras, Perfil   | Panel              | Seguimiento: ve el evento y su historial          |
| Operador      | Eventos, Cámaras, Perfil          | Eventos            | Tomar, Escalar y Marcar como atendido             |

Si un rol intenta abrir una sección que no le corresponde, la app lo devuelve a su pantalla de inicio.

### Pantallas

- **Ingreso:** el login de siempre, con el encabezado de Modo Guardián. Mantiene el ingreso con Google/Apple y "Regístrate".
- **Panel operacional** (Supervisor): eventos del día, alertas pendientes, atendidas, tiempo promedio de respuesta y las alertas críticas más recientes.
- **Eventos y alertas:** lista con filtros *Todos*, *Críticas*, *Pendientes* y *Atendidas*. Cada tarjeta muestra título, cámara, hora, estado y prioridad.
- **Detalle del evento:** prioridad, estado de atención, cámara, ubicación referencial e historial de acciones (quién, qué y cuándo). El Operador puede *Tomar evento*, *Escalar* y *Marcar como atendido* con una nota.
- **Cámaras:** estado de cada cámara (en línea o sin señal), última señal y alertas pendientes.
- **Usuarios y roles** (Administrador): lista los usuarios de prueba y permite cambiarles el rol. El cambio es simulado: vive en la memoria de la API de prueba y no modifica el rol con que ese usuario inicia sesión.
- **Perfil:** datos de la sesión, interruptor **Simular sin conexión**, botón **Enviar notificación de prueba**, accesos a los módulos de las guías anteriores y cierre de sesión.

Modelo de un evento: id, título, cámara, ubicación referencial, prioridad (crítica, media, baja), fecha y hora, estado de atención (pendiente, en atención, atendida) y registro de acciones.

### API REST (Guía 14)

`remote/ApiService.kt` declara los endpoints como funciones `suspend` de Retrofit y `remote/RetrofitInstance.kt` (un `object` con `lazy` y `GsonConverterFactory`) es el único lugar donde se define la URL base.

| Método | Ruta                        | Uso                                                        |
|--------|-----------------------------|------------------------------------------------------------|
| GET    | `eventos`                   | Lista de eventos y alertas                                 |
| GET    | `eventos/{id}`              | Detalle de un evento con su historial                      |
| POST   | `eventos/{id}/acciones`     | Registrar una acción (quién y qué; el cuándo lo pone la API) |
| PUT    | `eventos/{id}/estado`       | Cambiar el estado de atención                              |
| GET    | `camaras`                   | Estado de las cámaras                                      |
| GET    | `indicadores`               | Indicadores del panel                                      |
| GET    | `usuarios`                  | Usuarios de prueba (pantalla del Administrador)            |
| PUT    | `usuarios/{id}/rol`         | Cambiar el rol de un usuario de prueba                     |

**La API es simulada dentro de la app.** `remote/ApiSimulada.kt` es un interceptor de OkHttp que responde las llamadas de Retrofit con los datos ficticios de `DatosSimulados.kt` (guardados en memoria por `ServidorSimulado.kt`), con una pequeña demora para que se vea el estado de carga. Así el proyecto se puede ejecutar y evaluar sin levantar un servidor. Los cambios se reinician al cerrar la app.

Para usar un backend real que respete las mismas rutas, en `RetrofitInstance.kt` se cambia `USAR_API_SIMULADA` a `false` y se ajusta `BASE_URL` (desde el emulador, `http://10.0.2.2:PUERTO/` apunta al PC). `res/xml/network_security_config.xml` permite HTTP sin cifrar solo hacia `10.0.2.2` y `localhost`.

### Estados de pantalla y errores

- Los repositorios (`EventoRepository`, `CamaraRepository`, `UsuarioPruebaRepository`) reciben `ApiService` por constructor, y cada ViewModel recibe su repositorio por constructor.
- Cada ViewModel expone un `StateFlow` (con su `MutableStateFlow` privado) de tipo `UiState`: **Cargando**, **Exito** o **Error**.
- Los errores de conexión se muestran en pantalla con un aviso y el botón **Reintentar**; no se usa `println`.
- **Copia local:** cada carga correcta de eventos, cámaras e indicadores se guarda en una base Room aparte (`guardian_cache_db`). Si no hay conexión, la app muestra esos datos con el aviso "Sin conexión. Mostrando datos guardados.". La base `app_db` de las guías anteriores no se modificó.

### Notificación de prueba

El botón de Perfil pide el permiso de notificaciones (Android 13 o superior) y publica una notificación local con un dato ficticio. No usa servicios externos.

---

## Funcionalidades

### Login y roles (base de `main`)
- **Formulario de login** con campos de correo y contraseña (con opción de mostrar/ocultar la contraseña). Es la primera pantalla de la app.
- **Validaciones reactivas** desde el ViewModel, con errores visibles por campo (`isError` y `supportingText`). Al enviar se validan los campos; si un campo ya tiene error, se revalida mientras el usuario escribe.
- **Verificación de credenciales**: si el formulario es válido, se comprueban contra los usuarios de prueba y contra las cuentas creadas en la app. Si no coinciden, se muestra "Correo o contraseña incorrectos" (sin indicar cuál de los dos falló).
- **Roles de usuario** (Admin, Supervisor y Operador): el rol define las pestañas y acciones disponibles (ver *Modo Guardián*). Las cuentas creadas desde la app entran como **Operador**.
- **ViewModel compartido** (`LoginViewModel`) entre el login y las pantallas con sesión, sin pasar argumentos por la ruta. La antigua pantalla de bienvenida (`BienvenidaScreen`) se conserva en el código, pero el flujo ya no pasa por ella: sus accesos a los demás módulos están ahora en la pestaña **Perfil**.
- **Cierre de sesión** (desde Perfil) que limpia el estado y vuelve al login. La pila se limpia con `popUpTo`, así el botón "atrás" no vuelve al login tras ingresar ni a la app tras cerrar sesión.

### Crear cuenta (nuevo)
- Se accede desde el login con el botón **"¿No tienes cuenta? Regístrate"** (ruta `crear_cuenta`).
- Campos: **correo electrónico**, **contraseña**, **confirmar contraseña**, **nombre de usuario**, **país / región** (selector, Chile por defecto) y **preferencias de correo** (casilla opcional para recibir novedades).
- Contraseñas con opción de mostrar/ocultar y textos de ayuda bajo los campos de contraseña y usuario.
- Verifica que el correo y el nombre de usuario **no estén ya registrados**.
- La cuenta se guarda en **ROOM** (tabla `cuentas`); la contraseña **no se guarda en texto plano**, solo su hash SHA-256 con sal.
- Al terminar vuelve al login con el aviso "Cuenta creada. Ya puedes iniciar sesión.", y la nueva cuenta ya puede ingresar.

### Modo especial (Guía 12)
- Estado guardado con **DataStore** (`EstadoDataStore`), cargado con un loader simulado.
- Color animado (`animateColorAsState`), texto derivado (`derivedStateOf`), contador con `rememberSaveable` y mensaje de éxito con `AnimatedVisibility`.

### Perfil con cámara y galería (Guía 13)
- Selección de foto desde galería (`GetContent`) o cámara (`TakePicture` + `FileProvider`), con permiso de cámara en tiempo de ejecución.
- Foto mostrada con el componente reutilizable `ImagenInteligente`.

### Módulo integral con ROOM (Guía 14)
- **Registro integral** (se abre desde la pestaña Perfil) con foto (cámara o galería), nombre, correo, clave, confirmar clave, dirección y términos y condiciones.
- **Modo Premium** persistido en DataStore (`PreferencesManager`), con botón de color animado (gris → `#2E7D32`), mensaje de éxito que se oculta a los 2 segundos y `PremiumCard` animada.
- **Carga inicial** simulada de 1500 ms con `CircularProgressIndicator`.
- **Resumen** del registro leyendo el mismo `UsuarioViewModel`.
- **Listado offline** con `LazyColumn`, alimentado por un `Flow` de ROOM (funciona sin internet).
- La foto de galería se copia a la carpeta de la app para que siga disponible al cerrar y abrir.
- No se rompe al cancelar cámara o galería ni al rotar la pantalla.

---

## Tecnologías y Arquitectura

- **Lenguaje:** Kotlin
- **Interfaz de Usuario (UI):** Jetpack Compose con Material 3
- **Arquitectura:** MVVM (Model - View - ViewModel) con repositorio
- **Manejo de estado:** `MutableStateFlow` / `StateFlow` y `collectAsState()`
- **API REST:** Retrofit 2.11.0 con conversor Gson y OkHttp 4.12.0; corrutinas 1.8.0
- **Base de datos:** Room (con KSP). `app_db` versión 2 (tablas `usuarios` y `cuentas`) y `guardian_cache_db` versión 1 (copia local de eventos, cámaras e indicadores)
- **Preferencias:** DataStore Preferences
- **Imágenes:** Coil (`AsyncImage`)
- **Navegación:** Navigation Compose (`NavHost` y `NavController`)
- **Pruebas unitarias:** Kotest 5.8.0 (`StringSpec`), MockK 1.13.10, kotlinx-coroutines-test y JUnit 5
- **Pruebas de interfaz:** Compose UI Test con JUnit 4
- **Build System:** Gradle (Kotlin DSL - `build.gradle.kts`) con catálogo de versiones (`libs.versions.toml`)

---

## Estructura del Proyecto

El código fuente sigue los principios de MVVM para asegurar la separación de responsabilidades y la escalabilidad.

Ruta base: `app/src/main/java/com/example/dsy1105_002d_vgrinen_bsanhueza/`

- **`data/`**: Base de datos Room, entidades, DAOs y DataStore.
- **`model/`**: Clases de datos del estado de los formularios y modelos de Modo Guardián (evento, cámara, indicadores, `UiState`, permisos por rol).
- **`remote/`**: Consumo de la API REST: `ApiService`, `RetrofitInstance` y la API simulada.
- **`repository/`**: Operaciones de datos, mediador entre las fuentes de datos (Room o API) y la aplicación.
- **`viewmodel/`**: Lógica de presentación: estado de los formularios, reglas de validación, verificación de credenciales y estados de carga de la API.
- **`navegation/`**: Grafo de navegación de la app (`AppNavigation`) y nombres de las rutas (`Rutas`).
- **`notificaciones/`**: Notificación local de prueba.
- **`ui/`**: Componentes visuales de Jetpack Compose (`screens/`, `components/`, `utils/` y `theme/`).
- **`MainActivity.kt`**: Punto de entrada principal de la aplicación.

### Árbol de directorios

```text
app/src/main/java/com/example/dsy1105_002d_vgrinen_bsanhueza/
│
├── MainActivity.kt              # Crea AppDatabase, PreferencesManager, repositorios y factory; lanza AppNavigation
├── data/
│   ├── AppDatabase.kt           # Room v2 (usuarios + cuentas), singleton con getDatabase(context)
│   ├── UsuarioEntity.kt         # Tabla "usuarios" (registro integral)
│   ├── UsuarioDao.kt            # Flow de usuarios, getPorId, insertar, eliminar, buscarPorCorreo
│   ├── CuentaEntity.kt          # Tabla "cuentas" (Crear cuenta): correo y usuario únicos, clave con hash
│   ├── CuentaDao.kt             # Insertar y buscar por correo o nombre de usuario
│   ├── Seguridad.kt             # Sal aleatoria y hash SHA-256 de contraseñas
│   ├── Converters.kt            # TypeConverter Date <-> Long
│   ├── PreferencesManager.kt    # DataStore: modoEspecial (Guía 14)
│   └── EstadoDataStore.kt       # DataStore: estado del modo (Guía 12)
├── model/
│   ├── LoginUiState.kt, LoginErrores.kt, Usuario.kt, Rol.kt   # Login y roles
│   ├── CuentaUiState.kt, CuentaErrores.kt, Paises.kt          # Crear cuenta y lista de países
│   └── UsuarioUiState.kt, UsuarioErrores.kt                   # Registro integral
├── repository/
│   ├── UsuarioRepository.kt     # Mediador entre UsuarioDao y el ViewModel
│   └── CuentaRepository.kt      # Registrar cuentas, comprobar duplicados y autenticar
├── viewmodel/
│   ├── LoginViewModel.kt        # Login, validaciones, roles y verificación (+ Factory)
│   ├── RegistroCuentaViewModel.kt # Crear cuenta, validaciones y guardado en ROOM (+ Factory)
│   ├── EstadoViewModel.kt       # Modo especial (Guía 12)
│   ├── PerfilViewModel.kt       # Foto de perfil (Guía 13)
│   └── UsuarioViewModel.kt      # Registro integral, validación, DataStore y ROOM (+ Factory)
├── navegation/
│   └── AppNavigation.kt         # NavHost con todas las rutas
└── ui/
    ├── components/
    │   ├── ImagenInteligente.kt # Foto circular o icono Person
    │   └── PremiumCard.kt       # Tarjeta animada de beneficios Premium
    ├── screens/                 # Login, RegistroCuenta, Bienvenida, PantallaPrincipal, Perfil,
    │                            # RegistroIntegral, Resumen, ListadoOffline
    │                            # (HomeScreen*: variantes por tamaño de ventana, aún sin conectar a la navegación)
    ├── utils/                   # ImagenUtils (cámara/galería), WindowSizeUtils
    └── theme/                   # Colores, tipografía y tema
```

### Archivos de Modo Guardián

Se agregaron sin mover ni renombrar los anteriores:

```text
app/src/main/java/com/example/dsy1105_002d_vgrinen_bsanhueza/
│
├── data/
│   ├── GuardianCacheDatabase.kt # Room "guardian_cache_db" v1: copia local para usar sin conexión
│   ├── GuardianCacheDao.kt      # Leer y reemplazar eventos, cámaras e indicadores guardados
│   └── GuardianCacheEntities.kt # Tablas de la copia local y conversión desde/hacia los modelos
├── model/
│   ├── Evento.kt, AccionEvento.kt, Prioridad.kt, EstadoAtencion.kt   # Evento y su historial
│   ├── Camara.kt, Indicadores.kt, PanelDatos.kt, UsuarioPrueba.kt
│   ├── UiState.kt               # Cargando / Exito / Error
│   ├── FiltroEventos.kt         # Chips de la lista de eventos
│   ├── Permisos.kt              # Qué secciones ve cada rol y quién registra acciones
│   └── FechaHora.kt             # Hora corta a partir de la fecha ISO
├── remote/
│   ├── ApiService.kt            # Endpoints @GET, @POST y @PUT (suspend)
│   ├── RetrofitInstance.kt      # object con lazy, GsonConverterFactory y la URL base
│   ├── Solicitudes.kt           # Cuerpos de los POST y PUT
│   ├── ApiSimulada.kt           # Interceptor de OkHttp que responde sin servidor
│   ├── ServidorSimulado.kt      # Estado en memoria y cálculo de indicadores
│   └── DatosSimulados.kt        # Eventos, cámaras y usuarios ficticios
├── repository/
│   ├── EventoRepository.kt      # Eventos, indicadores, acciones (POST) y estado (PUT)
│   ├── CamaraRepository.kt
│   └── UsuarioPruebaRepository.kt
├── viewmodel/
│   ├── PanelViewModel.kt, EventosViewModel.kt, DetalleEventoViewModel.kt,
│   │   CamarasViewModel.kt, UsuariosViewModel.kt   # Cada uno con su Factory
│   └── MensajesDeError.kt       # Convierte un fallo de la API en el texto que ve el usuario
├── navegation/
│   └── Rutas.kt                 # Nombres de las rutas y pantalla de inicio según el rol
├── notificaciones/
│   └── NotificadorGuardian.kt   # Canal y notificación de prueba
└── ui/
    ├── components/              # BarraSuperiorGuardian, BarraInferiorGuardian, TarjetasGuardian,
    │                            # BotonesGuardian, EstadosGuardian (carga/error), ChipSeleccion
    ├── screens/                 # Panel, Eventos, DetalleEvento, Camaras, Usuarios, PerfilGuardian
    └── theme/                   # Color.kt y Theme.kt con la paleta del diseño

app/src/test/java/...            # Pruebas unitarias (Kotest + MockK)
app/src/androidTest/java/...     # Pruebas de interfaz (Compose UI Test)
```

Cada pantalla nueva tiene dos funciones: `XxxScreen`, que conecta el ViewModel, y `XxxContenido`, que solo dibuja el estado que recibe. Las pruebas de interfaz usan la segunda.

---

## Navegación

| Ruta            | Pantalla                 | Descripción                                          |
|-----------------|--------------------------|------------------------------------------------------|
| `login`         | `LoginScreen`            | Inicio de sesión (pantalla inicial)                  |
| `crear_cuenta`  | `RegistroCuentaScreen`   | Crear cuenta nueva (se llega desde el login)         |
| `panel`         | `PanelScreen`            | Indicadores y alertas críticas (Supervisor)          |
| `eventos`       | `EventosScreen`          | Eventos y alertas con filtros                        |
| `evento/{eventoId}` | `DetalleEventoScreen` | Detalle, acciones e historial de un evento          |
| `camaras`       | `CamarasScreen`          | Estado de las cámaras                                |
| `usuarios`      | `UsuariosScreen`         | Usuarios de prueba y roles (Administrador)           |
| `mi_perfil`     | `PerfilGuardianScreen`   | Sesión, herramientas de prueba y módulos anteriores  |
| `bienvenida`    | `BienvenidaScreen`       | Menú anterior; se conserva pero ya no se navega a él |
| `modo_especial` | `PantallaPrincipal`      | Guía 12: estado + DataStore + animaciones            |
| `perfil`        | `PerfilScreen`           | Guía 13: cámara y galería                            |
| `registro`      | `RegistroIntegralScreen` | Guía 14: formulario con foto y modo Premium          |
| `resumen`       | `ResumenScreen`          | Datos del registro recién guardado                   |
| `listado`       | `ListadoOfflineScreen`   | Usuarios guardados en ROOM                           |

Tras iniciar sesión se abre la pantalla de inicio del rol (`Rutas.inicioPara`). Las rutas `panel`, `eventos`, `camaras`, `usuarios` y `mi_perfil` llevan la barra inferior, que solo muestra las pestañas permitidas para el rol.

`LoginViewModel` se comparte entre `login` y las pantallas con sesión; `RegistroCuentaViewModel` se usa en `crear_cuenta`; `UsuarioViewModel` se comparte entre `registro`, `resumen` y `listado`. Al crear una cuenta, `crear_cuenta` avisa al `login` mediante el `savedStateHandle` y vuelve a él con `popBackStack()`.

---

## Reglas de validación

### Login
| Campo        | Reglas                                                 | Mensaje de error (ejemplo)                |
|--------------|--------------------------------------------------------|-------------------------------------------|
| Correo       | Obligatorio y con formato válido (`texto@dominio.ext`) | "Correo inválido (ej: nombre@dominio.cl)" |
| Contraseña   | Obligatoria                                            | "Campo obligatorio"                       |
| Credenciales | Deben coincidir con un usuario de prueba o una cuenta creada | "Correo o contraseña incorrectos"   |

**Notas:**

- El correo se valida con `trim()`, por lo que los espacios al inicio o al final no cuentan.
- En un login no se exigen reglas de complejidad de contraseña: eso corresponde al registro.
- Cada regla es una función privada `validarX()` en el ViewModel, que devuelve el mensaje de error o `null` si el valor es válido.
- El botón **Ingresar** solo navega (a la pantalla de inicio del rol) si las credenciales son correctas.

### Crear cuenta
| Campo               | Reglas                                                                                   | Mensaje de error                                                           |
|---------------------|------------------------------------------------------------------------------------------|----------------------------------------------------------------------------|
| Correo              | Obligatorio, formato válido y no registrado                                              | "Correo inválido (ej: nombre@dominio.cl)" / "Este correo ya está registrado" |
| Contraseña          | Mínimo 15 caracteres, **o** mínimo 8 con al menos un número y una letra minúscula        | "Mínimo 15 caracteres, o 8 con al menos un número y una minúscula"         |
| Confirmar contraseña| Obligatoria e igual a la contraseña                                                      | "Confirma tu contraseña" / "Las contraseñas no coinciden"                  |
| Nombre de usuario   | Solo letras, números y guiones simples; no puede empezar ni terminar con guion; no repetido | "Solo letras, números y guiones simples; no puede empezar ni terminar con guion" / "Ese nombre de usuario ya está en uso" |
| País / Región       | Se elige de una lista (Chile por defecto)                                                | —                                                                          |
| Preferencias de correo | Casilla opcional                                                                      | —                                                                          |

Al presionar **Crear cuenta** se valida todo el formulario; solo si es válido y no hay duplicados se guarda en ROOM y se vuelve al login.

### Registro integral
| Campo            | Reglas                         | Mensaje de error                  |
|------------------|--------------------------------|-----------------------------------|
| Nombre           | Mínimo 3 caracteres            | "Mínimo 3 caracteres"             |
| Correo           | Debe contener `@`              | "El correo debe contener @"       |
| Clave            | Mínimo 6 caracteres            | "Mínimo 6 caracteres"             |
| Confirmar clave  | Obligatoria e igual a la clave | "Las contraseñas no coinciden"    |
| Dirección        | No vacía                       | "La dirección es obligatoria"     |
| Términos         | Deben estar aceptados          | "Debes aceptar los términos"      |
| Foto             | Obligatoria                    | "Selecciona una foto"             |

Al presionar **Registrar y ver Resumen** se valida todo el formulario; solo si es válido se guarda en ROOM y se navega a `resumen`.

### Usuarios de prueba

Además de las cuentas creadas en la app, `LoginViewModel` valida contra una lista de usuarios de prueba definida en su `companion object`:

| Correo                     | Contraseña | Rol        | Qué ve al ingresar                                          |
|----------------------------|------------|------------|-------------------------------------------------------------|
| `admin@guardian.test`      | `123456`   | Admin      | Usuarios y roles; pestañas Usuarios y Perfil                |
| `supervisor@guardian.test` | `123456`   | Supervisor | Panel operacional; pestañas Panel, Eventos, Cámaras y Perfil |
| `operador@guardian.test`   | `123456`   | Operador   | Eventos y alertas; pestañas Eventos, Cámaras y Perfil       |

Son usuarios ficticios (dominio `guardian.test`) que solo existen dentro de la app.

---

## Permisos y FileProvider

- `AndroidManifest.xml` declara el permiso `CAMERA` y `uses-feature` de cámara como no obligatoria.
- El `FileProvider` (`res/xml/file_paths.xml`) expone la carpeta de imágenes de la app (`getExternalFilesDir`) para que la cámara escriba la foto sin permisos de almacenamiento.
- `INTERNET`: necesario para consumir la API REST con Retrofit.
- `POST_NOTIFICATIONS`: se pide al usuario recién cuando presiona **Enviar notificación de prueba**.
- `res/xml/network_security_config.xml`: permite HTTP sin cifrar únicamente hacia `10.0.2.2` y `localhost` (backend local durante el desarrollo).

---

## Instalación y Ejecución

1. Clona este repositorio:

```bash
git clone https://github.com/vicentegrinen/DSY1105-002D_VGrinen_BSanhueza.git
cd DSY1105-002D_VGrinen_BSanhueza
git checkout feature/modo-guardian
```

   (Mientras Modo Guardián no esté integrado en `main`, su código está en la rama `feature/modo-guardian`.)

2. Abre **Android Studio** (se recomienda la versión más reciente) con **Open** y navega hasta el directorio del proyecto clonado.
3. Espera a que Gradle sincronice las dependencias del proyecto.
4. Conecta un dispositivo Android físico con depuración USB habilitada (recomendado, por la cámara) o inicia un Emulador (AVD).
5. Haz clic en el botón **"Run"** (triángulo verde) en la barra de herramientas superior.

Requisitos: `minSdk 34`. No hace falta levantar ningún servidor: la API viene simulada dentro de la app.

El archivo `local.properties` (ruta del SDK) lo genera Android Studio en cada equipo y no se sube al repositorio.

> Al actualizar desde una versión anterior, la base de datos pasa de la versión 1 a la 2 y se recrea (se borran los datos de prueba guardados). Si hay problemas, desinstala la app del dispositivo y vuelve a ejecutarla.

---

## Cómo probar

### Modo Guardián
1. Ingresa con `operador@guardian.test` / `123456`: se abre **Eventos y alertas**. Prueba los filtros y abre un evento pendiente.
2. En el detalle presiona **Tomar evento**, escribe una nota y presiona **Marcar como atendido**: cambian el estado y el historial, y los botones quedan deshabilitados.
3. Revisa **Cámaras**: la cámara del evento atendido ya no muestra esa alerta pendiente.
4. En **Perfil** activa **Simular sin conexión** y vuelve a Eventos: aparece el aviso "Sin conexión. Mostrando datos guardados." con el botón **Reintentar**. Desactívalo y presiona Reintentar.
5. En **Perfil** presiona **Enviar notificación de prueba** y acepta el permiso.
6. Cierra sesión e ingresa con `supervisor@guardian.test`: aparece el **Panel** con los indicadores, y el detalle de un evento no tiene botones de acción.
7. Cierra sesión e ingresa con `admin@guardian.test`: solo ve **Usuarios** y **Perfil**, y puede cambiar el rol de los otros usuarios de prueba.

### Pruebas automáticas (Guía 15)

**Unitarias** (`app/src/test/java`, Kotest `StringSpec` + MockK + kotlinx-coroutines-test). Los ViewModels reciben el repositorio por constructor y el repositorio recibe `ApiService` por constructor, así que se reemplazan por mocks sin crear subclases falsas.

| Archivo                         | Qué comprueba                                                                 |
|---------------------------------|-------------------------------------------------------------------------------|
| `LoginViewModelTest`            | Validaciones, credenciales incorrectas y rol de cada usuario                  |
| `EventosViewModelTest`          | Carga, error de red (con y sin copia local), reintento y filtros              |
| `DetalleEventoViewModelTest`    | Carga, error de red y cambio de estado: tomar, escalar y marcar como atendido |
| `EventoRepositoryTest`          | Repositorio con `ApiService` simulado (`coEvery`): GET, POST, PUT y copia local |
| `PermisosTest`                  | Secciones que ve cada rol                                                     |

```bash
./gradlew :app:testDebugUnitTest
```

**De interfaz** (`app/src/androidTest/java`, Compose UI Test + JUnit 4). Necesitan un emulador o dispositivo conectado.

| Archivo                       | Qué comprueba                                                              |
|-------------------------------|----------------------------------------------------------------------------|
| `EventosContenidoTest`        | La lista muestra los títulos, abre el detalle y muestra el banner de error |
| `DetalleEventoContenidoTest`  | Botón "Marcar como atendido": visible, habilitado o no según el estado y el rol |

```bash
./gradlew :app:connectedDebugAndroidTest
```

En Windows el comando es `.\gradlew.bat` en lugar de `./gradlew`. En Android Studio también se pueden ejecutar con clic derecho sobre la carpeta `test` o `androidTest` → **Run tests**. Los informes quedan en `app/build/reports/`.

### Crear cuenta
1. En el login, presiona **"¿No tienes cuenta? Regístrate"**.
2. Presiona **Crear cuenta** con campos vacíos o inválidos para ver los mensajes de error (por ejemplo, contraseñas distintas o un usuario que empieza con guion).
3. Completa el formulario con datos válidos y crea la cuenta: vuelve al login con el aviso de cuenta creada.
4. Inicia sesión con el correo y la contraseña recién creados (entras como Operador).
5. Intenta registrar de nuevo el mismo correo o usuario: debe mostrar que ya están en uso.
6. En Android Studio, abre **App Inspection → Database Inspector** y revisa la tabla `cuentas` (la clave aparece como hash).

### Módulo integral
1. Inicia sesión con un usuario de prueba, abre la pestaña **Perfil** y entra a **Registro integral (Room + cámara)**.
2. Deja campos vacíos y presiona **Registrar y ver Resumen** para ver los mensajes de error.
3. Toma una foto con **Cámara** o elige una con **Galería**, completa el formulario y activa el **modo Premium** (observa el color animado y el mensaje).
4. Registra y revisa el **Resumen** y el **Listado offline** (también sin internet).
5. Cierra y abre la app: los registros (ROOM) y el modo Premium (DataStore) deben persistir.
6. En **Database Inspector**, revisa la tabla `usuarios`.

---

## Flujo de trabajo

- Rama de la actividad anterior: `Persistencia-y-Registro`
- Rama de Modo Guardián: `feature/modo-guardian` (se integra a `main` solo después de revisarla en equipo; sin force push ni reescritura de historial)
- Commits pequeños, uno por cambio lógico y en español
- Seguimiento de tareas en Trello (tarjetas: *Registro con ViewModel*, *Módulo Integral ROOM + Cámara + Animaciones*, *Consumo de API REST*, *Pantallas de Modo Guardián*, *Roles por pantalla*, *Pruebas unitarias y de interfaz*)

---

## Contribuidores

- Vicente Grinen (VGrinen)
- Benjamín Sanhueza (BSanhueza)

**Sección:** DSY1105-002D
