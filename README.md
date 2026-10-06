# DSY1105-002D_InsertCode

Aplicación Android desarrollada en Kotlin que implementa un flujo de registro de usuario con formulario reactivo, validaciones en tiempo real y una pantalla de resumen, aplicando una arquitectura limpia y moderna con las herramientas recomendadas por Google.

---

## Funcionalidades

- **Formulario de registro** con campos de nombre, correo, contraseña, dirección y aceptación de términos.
- **Validaciones reactivas** desde el ViewModel, con errores visibles por campo (`isError` y `supportingText`). Al enviar se validan todos los campos; si un campo ya tiene error, se revalida mientras el usuario escribe.
- **Validación de términos y condiciones**: no se puede registrar sin aceptarlos, con mensaje de error bajo el checkbox.
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

- **`model/`**: Clases de datos del estado del formulario (`UsuarioUiState`, `UsuarioErrores`, que incluye el error de términos).
- **`repository/`**: Operaciones de datos, mediador entre las fuentes de datos y la aplicación.
- **`viewmodel/`**: Lógica de presentación (`UsuarioViewModel`): estado del formulario y reglas de validación por campo.
- **`navegation/`**: Grafo de navegación de la app (`AppNavigation`).
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
│   └── UsuarioViewModel.kt   # Estado (StateFlow), validarFormulario() y reglas por campo.
├── navegation/
│   └── AppNavigation.kt      # NavHost con rutas "registro" y "resumen".
├── ui/
│   ├── screens/
│   │   ├── RegistroScreen.kt # Formulario con Material 3 y validaciones visibles.
│   │   └── ResumenScreen.kt  # Muestra los datos ingresados.
│   └── theme/                # Colores, tipografía y tema.
└── MainActivity.kt           # Punto de entrada, inicia la navegación.
```

---

## Reglas de validación

| Campo      | Reglas                                                                                   | Mensaje de error (ejemplo)                      |
|------------|------------------------------------------------------------------------------------------|-------------------------------------------------|
| Nombre     | Obligatorio, de 2 a 50 caracteres, solo letras, espacios, `'` y `-`                      | "Solo se permiten letras y espacios"            |
| Correo     | Obligatorio y con formato válido (`texto@dominio.ext`)                                   | "Correo inválido (ej: nombre@dominio.cl)"       |
| Contraseña | De 6 a 32 caracteres, sin espacios, con al menos una letra y un número                   | "Debe incluir al menos un número"               |
| Dirección  | Obligatoria, de 5 a 100 caracteres                                                       | "Ingresa una dirección más completa"            |
| Términos   | Deben estar aceptados para poder registrarse                                             | "Debes aceptar los términos y condiciones"      |

**Notas:**

- Nombre, correo y dirección se validan con `trim()`, por lo que los espacios al inicio o al final no cuentan.
- Cada regla es una función privada `validarX()` en `UsuarioViewModel`, que devuelve el mensaje de error o `null` si el valor es válido.
- El botón **Registrar** solo navega a `resumen` si `validarFormulario()` devuelve `true`.

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