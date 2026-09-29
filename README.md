# DSY1105-002D_InsertCode

Un proyecto de aplicación Android desarrollado en Kotlin, diseñado para demostrar la implementación de una arquitectura limpia y moderna utilizando las últimas herramientas recomendadas por Google.

---

## Tecnologías y Arquitectura

Este proyecto está construido con las siguientes tecnologías:

- **Lenguaje:** Kotlin
- **Interfaz de Usuario (UI):** Jetpack Compose (HomeScreen, Theme, Typography)
- **Arquitectura:** MVVM (Model - View - ViewModel)
- **Build System:** Gradle (Kotlin DSL - `build.gradle.kts`)

---

## Estructura del Proyecto

El código fuente está organizado siguiendo los principios de la arquitectura MVVM para asegurar la separación de responsabilidades y la escalabilidad del código.

Ruta base: `app/src/main/java/com/example/.../`

- **`model/`**: Contiene las clases de datos y la lógica de negocio pura.
- **`repository/`**: Maneja las operaciones de datos, actuando como mediador entre diferentes fuentes de datos y la aplicación.
- **`viewmodel/`**: Contiene la lógica de presentación, conectando el modelo de datos (Repository) con la interfaz de usuario (UI).
- **`ui/`**: Contiene los componentes visuales de Jetpack Compose (pantallas y temas).
- **`MainActivity.kt`**: Punto de entrada principal de la aplicación.

### Árbol de directorios

```text
app/src/main/java/com/example/dsy1105_002d_vgrinen_bsanhueza/
│
├── model/         # Clases de datos y la lógica de negocio pura.
├── repository/    # Operaciones de datos y conexión con APIs/BD.
├── viewmodel/     # Lógica de presentación, conecta el Repository con la UI.
├── ui/            # Componentes visuales de Jetpack Compose (pantallas y temas).
└── MainActivity   # Punto de entrada principal de la aplicación.
```

---

## Instalación y Ejecución

Para clonar y ejecutar este proyecto en tu entorno local, sigue estos pasos:

1. Clona este repositorio:

```bash
   git clone https://github.com/vicentegrinen/DSY1105-002D_VGrinen_BSanhueza.git
```

2. Abre **Android Studio** (se recomienda la versión más reciente, Ladybug o Koala).
3. Selecciona **"Open"** y navega hasta el directorio del proyecto clonado.
4. Espera a que Gradle sincronice las dependencias del proyecto.
5. Conecta un dispositivo Android físico con depuración USB habilitada o inicia un Emulador (AVD).
6. Haz clic en el botón **"Run"** (triángulo verde) en la barra de herramientas superior de Android Studio.

---

## Contribuidores

- Vicente Grinen (VGrinen)
- Benjamín Sanhueza (BSanhueza)

**Sección:** DSY1105-002D
