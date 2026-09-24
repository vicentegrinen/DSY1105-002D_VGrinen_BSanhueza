# DSY1105-002D_InsertCode
Un proyecto de aplicación Android desarrollado en Kotlin, diseñado para demostrar la implementación de una arquitectura limpia y moderna utilizando las últimas herramientas recomendadas por Google.

#Tecnologías y Arquitectura
Este proyecto está construido con las siguientes tecnologías:
Lenguaje: Kotlin
Interfaz de Usuario (UI): Jetpack Compose (HomeScreen, Theme, Typography)
Arquitectura: MVVM (Model - View - ViewModel)
Build System: Gradle (Kotlin DSL - build.gradle.kts)

#Estructura del Proyecto
El código fuente está organizado siguiendo los principios de la arquitectura MVVM para asegurar la separación de responsabilidades y la escalabilidad del código:
app/src/main/java/com/example/.../
model/: Contiene las clases de datos y la lógica de negocio pura.
repository/: Maneja las operaciones de datos, actuando como mediador entre diferentes fuentes de datos y la aplicación.
viewmodel/: Contiene la lógica de presentación, conectando el modelo de datos (Repository) con la interfaz de usuario (UI).
ui/: Contiene los componentes visuales de Jetpack Compose (pantallas y temas).
MainActivity.kt: Punto de entrada principal de la aplicación.

#Instalación y Ejecución

Para clonar y ejecutar este proyecto en tu entorno local, sigue estos pasos:
Clona este repositorio:
git clone https://github.com/tu-usuario/tu-repositorio.git
Abre Android Studio (se recomienda la versión más reciente, Ladybug o Koala).
Selecciona "Open" y navega hasta el directorio del proyecto clonado.
Espera a que Gradle sincronice las dependencias del proyecto.
Conecta un dispositivo Android físico con depuración USB habilitada o inicia un Emulador (AVD).
Haz clic en el botón "Run" (triángulo verde) en la barra de herramientas superior de Android Studio.

# Contribudores
Vicente Grinen (VGrinen)
Benjamín Sanhueza (BSanhueza)
Sección: DSY1105-002D
