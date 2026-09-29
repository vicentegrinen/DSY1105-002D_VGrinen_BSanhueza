# DSY1105-002D_InsertCode

Aplicación Android desarrollada en Kotlin que implementa un flujo de registro de usuario con formulario reactivo, validaciones en tiempo real y una pantalla de resumen, aplicando una arquitectura limpia y moderna con las herramientas recomendadas por Google.

---

## Funcionalidades

- **Formulario de registro** con campos de nombre, correo, contraseña, dirección y aceptación de términos.
- **Validaciones reactivas** desde el ViewModel, con errores visibles por campo (`isError` y `supportingText`).
- **Pantalla de resumen** que muestra los datos ingresados, usando un **ViewModel compartido** entre pantallas (sin pasar argumentos por la ruta).
- **Navegación** entre las pantallas `registro` y `resumen` con Navigation Compose.

---

## Tecnologías y Arquitectura

- **Lenguaje:** Kotlin
- **Interfaz de Usuario (UI):** Jetpack Compose con Material 3 (RegistroScreen, ResumenScreen, Theme, Typography)
- **Arquitectura:** MVVM (Model - View - ViewModel)
- **Manejo de estado:** `MutableStateFlow` / `StateFlow` y `collectAsState()`
- **Navegación:** Navigation Compose 2.7.x (`NavHost` y `NavController`)
- **Build System:** Gradle (Kotlin DSL - `build.gradle.kts`)

---

## Estructura del Proyecto

El código fuente sigue los principios de MVVM para asegurar la separación de responsabilidades y la escalabilidad.

Ruta base: `app/src/main/java/com/example/dsy1105_002d_vgrinen_bsanhueza/`

- **`model/`**: Clases de datos del estado del formulario (`UsuarioUiState`, `UsuarioErrores`).
- **`repository/`**: Operaciones de datos, mediador entre las fuentes de datos y la aplicación.
- **`viewmodel/`**: Lógica de presentación (`UsuarioViewModel`): estado del formulario y validaciones.
- **`navigation/`**: Grafo de navegación de la app (`AppNavigation`).
- **`ui/`**: Componentes visuales de Jetpack Compose (`screens/` y `theme/`).
- **`MainActivity.kt`**: Punto de entrada principal de la aplicación.

### Árbol de directorios

```text
app/src/main/java/com/example/dsy1105_002d_vgrinen_bsanhueza/
│
├── model/
│   ├── UsuarioErrores.kt     # Errores de validación por campo.
│   └── UsuarioUiState.kt     # Estado completo del formulario.
├── repository/               # Operaciones de datos y conexión con APIs/BD.
├── viewmodel/
│   └── UsuarioViewModel.kt   # Estado (StateFlow) y validarFormulario().
├── navigation/
│   └── AppNavigation.kt      # NavHost con rutas "registro" y "resumen".
├── ui/
│   ├── screens/
│   │   ├── RegistroScreen.kt # Formulario con Material 3.
│   │   └── ResumenScreen.kt  # Muestra los datos ingresados.
│   └── theme/                # Colores, tipografía y tema.
└── MainActivity.kt           # Punto de entrada, inicia la navegación.
```

---

## Reglas de validación

| Campo      | Regla                                  |
|------------|----------------------------------------|
| Nombre     | Obligatorio                            |
| Correo     | Debe contener `@`                      |
| Contraseña | Mínimo 6 caracteres                    |
| Dirección  | Obligatoria                            |

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