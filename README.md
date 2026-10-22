# DSY1105-002D · Persistencia y Registro

Aplicación Android en Kotlin (Jetpack Compose + Material 3) que integra, en un solo proyecto, el trabajo de las guías de la asignatura **Desarrollo de Aplicaciones Móviles**: inicio de sesión con roles, estado persistente con DataStore, cámara y galería, y un módulo de registro de usuarios guardado offline con ROOM.

---

## Funcionalidades

### Login y roles
- Formulario de login con correo y contraseña (mostrar/ocultar contraseña).
- Validaciones reactivas desde el ViewModel (`isError` y `supportingText`) y verificación de credenciales con el mensaje genérico "Correo o contraseña incorrectos".
- Tres roles (Admin, Supervisor, Operador); la pantalla de bienvenida cambia título y mensaje según el rol.
- Cierre de sesión que limpia el estado y la pila de navegación (`popUpTo`).

### Modo especial (Guía 12)
- Estado guardado con **DataStore** (`EstadoDataStore`), cargado con un loader simulado.
- Color animado (`animateColorAsState`), texto derivado (`derivedStateOf`), contador con `rememberSaveable` y mensaje de éxito con `AnimatedVisibility`.

### Perfil con cámara y galería (Guía 13)
- Selección de foto desde galería (`GetContent`) o cámara (`TakePicture` + `FileProvider`), con permiso de cámara en tiempo de ejecución.
- Foto mostrada con el componente reutilizable `ImagenInteligente`.

### Módulo integral con ROOM (Guía 14)
- **Registro** con foto (cámara o galería), nombre, correo, clave, confirmar clave, dirección y términos y condiciones.
- **Modo Premium** persistido en DataStore (`PreferencesManager`), con botón de color animado (gris → `#2E7D32`), mensaje de éxito que se oculta a los 2 segundos y `PremiumCard` animada.
- **Carga inicial** simulada de 1500 ms con `CircularProgressIndicator`.
- **Resumen** del registro leyendo el mismo `UsuarioViewModel`.
- **Listado offline** con `LazyColumn`, alimentado por un `Flow` de ROOM (funciona sin internet).
- La foto de galería se copia a la carpeta de la app para que siga disponible al cerrar y abrir.
- No se rompe al cancelar cámara o galería ni al rotar la pantalla.

---

## Reglas de validación

### Login
| Campo        | Reglas                                                 | Mensaje de error (ejemplo)                |
|--------------|--------------------------------------------------------|-------------------------------------------|
| Correo       | Obligatorio y con formato válido                       | "Correo inválido (ej: nombre@dominio.cl)" |
| Contraseña   | Obligatoria                                            | "Campo obligatorio"                       |
| Credenciales | Deben coincidir con un usuario existente               | "Correo o contraseña incorrectos"         |

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

### Usuarios de prueba (login)
No hay backend: `LoginViewModel` valida contra una lista de prueba.

| Correo                     | Contraseña | Rol        |
|----------------------------|------------|------------|
| `admin@guardian.test`      | `123456`   | Admin      |
| `supervisor@guardian.test` | `123456`   | Supervisor |
| `operador@guardian.test`   | `123456`   | Operador   |

---

## Navegación

| Ruta            | Pantalla                  | Descripción                                   |
|-----------------|---------------------------|-----------------------------------------------|
| `login`         | `LoginScreen`             | Inicio de sesión (pantalla inicial)           |
| `bienvenida`    | `BienvenidaScreen`        | Menú según el rol, accesos a las demás pantallas |
| `modo_especial` | `PantallaPrincipal`       | Guía 12: estado + DataStore + animaciones     |
| `perfil`        | `PerfilScreen`            | Guía 13: cámara y galería                     |
| `registro`      | `RegistroIntegralScreen`  | Guía 14: formulario con foto y modo Premium   |
| `resumen`       | `ResumenScreen`           | Datos del registro recién guardado            |
| `listado`       | `ListadoOfflineScreen`    | Usuarios guardados en ROOM                    |

`LoginViewModel` se comparte entre `login` y `bienvenida`; `UsuarioViewModel` se comparte entre `registro`, `resumen` y `listado`.

---

## Tecnologías y arquitectura

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose con Material 3
- **Arquitectura:** MVVM (Model - View - ViewModel) con repositorio
- **Estado:** `MutableStateFlow` / `StateFlow` y `collectAsState()`
- **Base de datos:** Room (con KSP)
- **Preferencias:** DataStore Preferences
- **Imágenes:** Coil (`AsyncImage`)
- **Navegación:** Navigation Compose
- **Build:** Gradle (Kotlin DSL) con catálogo de versiones (`libs.versions.toml`)

---

## Estructura del proyecto

Ruta base: `app/src/main/java/com/example/dsy1105_002d_vgrinen_bsanhueza/`

```text
├── MainActivity.kt              # Crea AppDatabase, PreferencesManager, repositorio y factory; lanza AppNavigation
├── data/
│   ├── AppDatabase.kt           # Room, singleton @Volatile con getDatabase(context)
│   ├── UsuarioEntity.kt         # Tabla "usuarios"
│   ├── UsuarioDao.kt            # Flow de usuarios, getPorId, insertar (REPLACE), eliminar, buscarPorCorreo
│   ├── Converters.kt            # TypeConverter Date <-> Long
│   ├── PreferencesManager.kt    # DataStore: modoEspecial (Guía 14)
│   └── EstadoDataStore.kt       # DataStore: estado del modo (Guía 12)
├── model/
│   ├── LoginUiState.kt, LoginErrores.kt, Usuario.kt, Rol.kt   # Login y roles
│   └── UsuarioUiState.kt, UsuarioErrores.kt                   # Registro integral
├── repository/
│   └── UsuarioRepository.kt     # Mediador entre el DAO y el ViewModel
├── viewmodel/
│   ├── LoginViewModel.kt        # Login, validaciones y roles
│   ├── EstadoViewModel.kt       # Modo especial (Guía 12)
│   ├── PerfilViewModel.kt       # Foto de perfil (Guía 13)
│   └── UsuarioViewModel.kt      # Registro, validación, DataStore y ROOM (+ Factory)
├── navegation/
│   └── AppNavigation.kt         # NavHost con todas las rutas
└── ui/
    ├── components/
    │   ├── ImagenInteligente.kt # Foto circular o icono Person
    │   └── PremiumCard.kt       # Tarjeta animada de beneficios Premium
    ├── screens/                 # Login, Bienvenida, PantallaPrincipal, Perfil,
    │                            # RegistroIntegral, Resumen, ListadoOffline
    │                            # (HomeScreen*: variantes por tamaño de ventana, aún sin conectar a la navegación)
    ├── utils/                   # ImagenUtils (cámara/galería), WindowSizeUtils
    └── theme/                   # Colores, tipografía y tema
```

---

## Permisos y FileProvider

- `AndroidManifest.xml` declara el permiso `CAMERA` y `uses-feature` de cámara como no obligatoria.
- El `FileProvider` (`res/xml/file_paths.xml`) expone la carpeta de imágenes de la app (`getExternalFilesDir`) para que la cámara escriba la foto sin permisos de almacenamiento.

---

## Instalación y ejecución

1. Clona el repositorio:

```bash
git clone https://github.com/vicentegrinen/DSY1105-002D_VGrinen_BSanhueza.git
```

2. Abre el proyecto en **Android Studio** (versión reciente) con **Open** y espera a que Gradle sincronice.
3. Conecta un dispositivo Android físico con depuración USB (recomendado, por la cámara) o inicia un emulador.
4. Presiona **Run**.

Requisitos: `minSdk 34`.

---

## Cómo probar el módulo integral

1. Inicia sesión con un usuario de prueba y entra a **Registro integral**.
2. Deja campos vacíos y presiona **Registrar y ver Resumen** para ver los mensajes de error.
3. Toma una foto con **Cámara** o elige una con **Galería**, completa el formulario y activa el **modo Premium** (observa el color animado y el mensaje).
4. Registra y revisa el **Resumen** y el **Listado offline** (también sin internet).
5. Cierra y abre la app: los registros (ROOM) y el modo Premium (DataStore) deben persistir.
6. En Android Studio, abre **App Inspection → Database Inspector** y revisa la tabla `usuarios`.

---

## Flujo de trabajo

- Rama de la actividad: `feature/modulo-integral-room`
- Seguimiento en Trello (tarjeta: *Módulo Integral ROOM + Cámara + Animaciones*)
- Commit final: "Módulo integral ROOM + Cámara + Animaciones funcional"

---

## Contribuidores

- Vicente Grinen (VGrinen)
- Benjamín Sanhueza (BSanhueza)

**Sección:** DSY1105-002D
