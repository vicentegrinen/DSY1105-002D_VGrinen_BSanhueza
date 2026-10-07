# DSY1105-002D_InsertCode

Aplicación Android desarrollada en Kotlin que implementa un flujo de inicio de sesión (login) con formulario reactivo, validaciones en tiempo real y una pantalla de bienvenida, aplicando una arquitectura limpia y moderna con las herramientas recomendadas por Google.

---

## Funcionalidades

- **Formulario de login** con campos de correo y contraseña (con opción de mostrar/ocultar la contraseña).
- **Validaciones reactivas** desde el ViewModel, con errores visibles por campo (`isError` y `supportingText`). Al enviar se validan los campos; si un campo ya tiene error, se revalida mientras el usuario escribe.
- **Verificación de credenciales**: si el formulario es válido, se comprueban las credenciales y, si no coinciden, se muestra el error "Correo o contraseña incorrectos" (sin indicar cuál de los dos falló).
- **Roles de usuario** (Admin, Supervisor y Operador): cada usuario tiene un rol y la pantalla de bienvenida muestra un título y un mensaje distintos según el rol.
- **Pantalla de bienvenida** que muestra la sesión iniciada, usando un **ViewModel compartido** entre pantallas (sin pasar argumentos por la ruta).
- **Cierre de sesión** que limpia el estado y vuelve al login.
- **Navegación** entre las pantallas `login` y `bienvenida` con Navigation Compose. La pila se limpia con `popUpTo`, así el botón "atrás" no vuelve al login tras ingresar ni a la bienvenida tras cerrar sesión.

---

## Tecnologías y Arquitectura

- **Lenguaje:** Kotlin
- **Interfaz de Usuario (UI):** Jetpack Compose con Material 3 (LoginScreen, BienvenidaScreen, Theme, Typography)
- **Arquitectura:** MVVM (Model - View - ViewModel)
- **Manejo de estado:** `MutableStateFlow` / `StateFlow` y `collectAsState()`
- **Navegación:** Navigation Compose 2.7.x (`NavHost` y `NavController`)
- **Build System:** Gradle (Kotlin DSL - `build.gradle.kts`)

---

## Estructura del Proyecto

El código fuente sigue los principios de MVVM para asegurar la separación de responsabilidades y la escalabilidad.

Ruta base: `app/src/main/java/com/example/dsy1105_002d_vgrinen_bsanhueza/`

- **`model/`**: Clases de datos del estado del formulario (`LoginUiState`, `LoginErrores`, `Usuario`, `Rol`).
- **`repository/`**: Operaciones de datos, mediador entre las fuentes de datos y la aplicación.
- **`viewmodel/`**: Lógica de presentación (`LoginViewModel`): estado del formulario, reglas de validación y verificación de credenciales.
- **`navegation/`**: Grafo de navegación de la app (`AppNavigation`).
- **`ui/`**: Componentes visuales de Jetpack Compose (`screens/` y `theme/`).
- **`MainActivity.kt`**: Punto de entrada principal de la aplicación.

### Árbol de directorios

```text
app/src/main/java/com/example/dsy1105_002d_vgrinen_bsanhueza/
│
├── model/
│   ├── LoginErrores.kt       # Errores por campo y error general de credenciales.
│   ├── LoginUiState.kt       # Estado del login (incluye el rol del usuario autenticado).
│   ├── Rol.kt                # Enum de roles: Admin, Supervisor, Operador.
│   └── Usuario.kt            # Correo, contraseña y rol de un usuario.
├── repository/               # Operaciones de datos y conexión con APIs/BD.
├── viewmodel/
│   └── LoginViewModel.kt     # Estado (StateFlow), iniciarSesion(), cerrarSesion() y reglas.
├── navegation/
│   └── AppNavigation.kt      # NavHost con rutas "login" y "bienvenida".
├── ui/
│   ├── screens/
│   │   ├── LoginScreen.kt    # Formulario con Material 3 y validaciones visibles.
│   │   └── BienvenidaScreen.kt # Mensaje de inicio según el rol y cierre de sesión.
│   └── theme/                # Colores, tipografía y tema.
└── MainActivity.kt           # Punto de entrada, inicia la navegación.
```

---

## Reglas de validación

| Campo        | Reglas                                                 | Mensaje de error (ejemplo)                |
|--------------|--------------------------------------------------------|-------------------------------------------|
| Correo       | Obligatorio y con formato válido (`texto@dominio.ext`) | "Correo inválido (ej: nombre@dominio.cl)" |
| Contraseña   | Obligatoria                                            | "Campo obligatorio"                       |
| Credenciales | Deben coincidir con un usuario existente               | "Correo o contraseña incorrectos"         |

**Notas:**

- El correo se valida con `trim()`, por lo que los espacios al inicio o al final no cuentan.
- En un login no se exigen reglas de complejidad de contraseña (largo, letras, números): eso corresponde al registro.
- Cada regla es una función privada `validarX()` en `LoginViewModel`, que devuelve el mensaje de error o `null` si el valor es válido.
- El botón **Ingresar** solo navega a `bienvenida` si `iniciarSesion()` devuelve `true`.

### Usuarios de prueba

Como la app no tiene backend, `LoginViewModel` valida contra una lista de usuarios de prueba definida en su `companion object`:

| Correo                     | Contraseña | Rol        | Mensaje de inicio                                                      |
|----------------------------|------------|------------|------------------------------------------------------------------------|
| `admin@guardian.test`      | `123456`   | Admin      | "Panel de Administración": acceso total a usuarios y configuración     |
| `supervisor@guardian.test` | `123456`   | Supervisor | "Panel de Supervisión": revisar operaciones y coordinar al equipo      |
| `operador@guardian.test`   | `123456`   | Operador   | "Panel de Operación": todo listo para comenzar el turno                |

Para conectar un backend real, reemplaza esa lista por una consulta a un repositorio (`repository/`) o a una API.

---

## Instalación y Ejecución

1. Clona este repositorio:

```bash
git clone https://github.com/vicentegrinen/DSY1105-002D_VGrinen_BSanhueza.git
```

2. Abre **Android Studio** (se recomienda la versión más reciente, Ladybug o Koala).
3. Selecciona **"Open"** y navega hasta el directorio del proyecto clonado.
4. Espera a que Gradle sincronice las dependencias del proyecto.
5. Conecta un dispositivo Android físico con depuración USB habilitada o inicia un Emulador (AVD).
6. Haz clic en el botón **"Run"** (triángulo verde) en la barra de herramientas superior.

---

## Flujo de trabajo

- Rama de la actividad: `feature/registro-formulario`
- Seguimiento de tareas en Trello (tarjeta: *Registro con ViewModel*)

---

## Contribuidores

- Vicente Grinen (VGrinen)
- Benjamín Sanhueza (BSanhueza)

**Sección:** DSY1105-002D