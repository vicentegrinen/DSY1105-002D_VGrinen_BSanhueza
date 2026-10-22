# DSY1105-002D_VGrinen_BSanhueza

Aplicación Android desarrollada en Kotlin que parte del flujo de inicio de sesión (login) de la rama `main` (formulario reactivo, validaciones en tiempo real, roles y pantalla de bienvenida) y le suma **creación de cuentas**, **persistencia local con ROOM y DataStore**, **cámara y galería** y un **módulo de registro integral**, aplicando una arquitectura limpia y moderna con las herramientas recomendadas por Google.

---

## Funcionalidades

### Login y roles (base de `main`)
- **Formulario de login** con campos de correo y contraseña (con opción de mostrar/ocultar la contraseña). Es la primera pantalla de la app.
- **Validaciones reactivas** desde el ViewModel, con errores visibles por campo (`isError` y `supportingText`). Al enviar se validan los campos; si un campo ya tiene error, se revalida mientras el usuario escribe.
- **Verificación de credenciales**: si el formulario es válido, se comprueban contra los usuarios de prueba y contra las cuentas creadas en la app. Si no coinciden, se muestra "Correo o contraseña incorrectos" (sin indicar cuál de los dos falló).
- **Roles de usuario** (Admin, Supervisor y Operador): la pantalla de bienvenida muestra un título y un mensaje distintos según el rol. Las cuentas creadas desde la app entran como **Operador**.
- **Pantalla de bienvenida** con un **ViewModel compartido** entre pantallas (sin pasar argumentos por la ruta) y accesos a los demás módulos.
- **Cierre de sesión** que limpia el estado y vuelve al login. La pila se limpia con `popUpTo`, así el botón "atrás" no vuelve al login tras ingresar ni a la bienvenida tras cerrar sesión.

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
- **Registro integral** (se abre desde Bienvenida) con foto (cámara o galería), nombre, correo, clave, confirmar clave, dirección y términos y condiciones.
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
- **Base de datos:** Room (con KSP), versión 2 (tablas `usuarios` y `cuentas`)
- **Preferencias:** DataStore Preferences
- **Imágenes:** Coil (`AsyncImage`)
- **Navegación:** Navigation Compose (`NavHost` y `NavController`)
- **Build System:** Gradle (Kotlin DSL - `build.gradle.kts`) con catálogo de versiones (`libs.versions.toml`)

---

## Estructura del Proyecto

El código fuente sigue los principios de MVVM para asegurar la separación de responsabilidades y la escalabilidad.

Ruta base: `app/src/main/java/com/example/dsy1105_002d_vgrinen_bsanhueza/`

- **`data/`**: Base de datos Room, entidades, DAOs y DataStore.
- **`model/`**: Clases de datos del estado de los formularios.
- **`repository/`**: Operaciones de datos, mediador entre las fuentes de datos y la aplicación.
- **`viewmodel/`**: Lógica de presentación: estado de los formularios, reglas de validación y verificación de credenciales.
- **`navegation/`**: Grafo de navegación de la app (`AppNavigation`).
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

---

## Navegación

| Ruta            | Pantalla                 | Descripción                                          |
|-----------------|--------------------------|------------------------------------------------------|
| `login`         | `LoginScreen`            | Inicio de sesión (pantalla inicial)                  |
| `crear_cuenta`  | `RegistroCuentaScreen`   | Crear cuenta nueva (se llega desde el login)         |
| `bienvenida`    | `BienvenidaScreen`       | Menú según el rol, accesos a las demás pantallas     |
| `modo_especial` | `PantallaPrincipal`      | Guía 12: estado + DataStore + animaciones            |
| `perfil`        | `PerfilScreen`           | Guía 13: cámara y galería                            |
| `registro`      | `RegistroIntegralScreen` | Guía 14: formulario con foto y modo Premium          |
| `resumen`       | `ResumenScreen`          | Datos del registro recién guardado                   |
| `listado`       | `ListadoOfflineScreen`   | Usuarios guardados en ROOM                           |

`LoginViewModel` se comparte entre `login` y `bienvenida`; `RegistroCuentaViewModel` se usa en `crear_cuenta`; `UsuarioViewModel` se comparte entre `registro`, `resumen` y `listado`. Al crear una cuenta, `crear_cuenta` avisa al `login` mediante el `savedStateHandle` y vuelve a él con `popBackStack()`.

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
- El botón **Ingresar** solo navega a `bienvenida` si las credenciales son correctas.

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

| Correo                     | Contraseña | Rol        | Mensaje de inicio                                                      |
|----------------------------|------------|------------|------------------------------------------------------------------------|
| `admin@guardian.test`      | `123456`   | Admin      | "Panel de Administración": acceso total a usuarios y configuración     |
| `supervisor@guardian.test` | `123456`   | Supervisor | "Panel de Supervisión": revisar operaciones y coordinar al equipo      |
| `operador@guardian.test`   | `123456`   | Operador   | "Panel de Operación": todo listo para comenzar el turno                |

---

## Permisos y FileProvider

- `AndroidManifest.xml` declara el permiso `CAMERA` y `uses-feature` de cámara como no obligatoria.
- El `FileProvider` (`res/xml/file_paths.xml`) expone la carpeta de imágenes de la app (`getExternalFilesDir`) para que la cámara escriba la foto sin permisos de almacenamiento.

---

## Instalación y Ejecución

1. Clona este repositorio:

```bash
git clone https://github.com/vicentegrinen/DSY1105-002D_VGrinen_BSanhueza.git
git checkout Persistencia-y-Registro
```

2. Abre **Android Studio** (se recomienda la versión más reciente) con **Open** y navega hasta el directorio del proyecto clonado.
3. Espera a que Gradle sincronice las dependencias del proyecto.
4. Conecta un dispositivo Android físico con depuración USB habilitada (recomendado, por la cámara) o inicia un Emulador (AVD).
5. Haz clic en el botón **"Run"** (triángulo verde) en la barra de herramientas superior.

Requisitos: `minSdk 34`.

> Al actualizar desde una versión anterior, la base de datos pasa de la versión 1 a la 2 y se recrea (se borran los datos de prueba guardados). Si hay problemas, desinstala la app del dispositivo y vuelve a ejecutarla.

---

## Cómo probar

### Crear cuenta
1. En el login, presiona **"¿No tienes cuenta? Regístrate"**.
2. Presiona **Crear cuenta** con campos vacíos o inválidos para ver los mensajes de error (por ejemplo, contraseñas distintas o un usuario que empieza con guion).
3. Completa el formulario con datos válidos y crea la cuenta: vuelve al login con el aviso de cuenta creada.
4. Inicia sesión con el correo y la contraseña recién creados (entras como Operador).
5. Intenta registrar de nuevo el mismo correo o usuario: debe mostrar que ya están en uso.
6. En Android Studio, abre **App Inspection → Database Inspector** y revisa la tabla `cuentas` (la clave aparece como hash).

### Módulo integral
1. Inicia sesión con un usuario de prueba y entra a **Registro integral**.
2. Deja campos vacíos y presiona **Registrar y ver Resumen** para ver los mensajes de error.
3. Toma una foto con **Cámara** o elige una con **Galería**, completa el formulario y activa el **modo Premium** (observa el color animado y el mensaje).
4. Registra y revisa el **Resumen** y el **Listado offline** (también sin internet).
5. Cierra y abre la app: los registros (ROOM) y el modo Premium (DataStore) deben persistir.
6. En **Database Inspector**, revisa la tabla `usuarios`.

---

## Flujo de trabajo

- Rama de la actividad: `Persistencia-y-Registro`
- Seguimiento de tareas en Trello (tarjetas: *Registro con ViewModel*, *Módulo Integral ROOM + Cámara + Animaciones*)

---

## Contribuidores

- Vicente Grinen (VGrinen)
- Benjamín Sanhueza (BSanhueza)

**Sección:** DSY1105-002D
