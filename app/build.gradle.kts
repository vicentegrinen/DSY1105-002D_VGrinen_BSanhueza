plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.dsy1105_002d_vgrinen_bsanhueza"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.dsy1105_002d_vgrinen_bsanhueza"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

// Guía 15: las pruebas unitarias (src/test) corren sobre JUnit Platform (Kotest + JUnit5)
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.window.size.class1)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    // Guía 15: Kotest (StringSpec) + MockK + corrutinas de prueba + JUnit5
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    // Mantiene ejecutables las pruebas JUnit4 existentes (ExampleUnitTest) sobre JUnit Platform
    testRuntimeOnly(libs.junit.vintage.engine)
    // Gradle 9 ya no agrega el launcher por su cuenta
    testRuntimeOnly(libs.junit.platform.launcher)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    // Guía 14: corrutinas 1.8.0 (la versión viene del catálogo)
    implementation(libs.kotlinx.coroutines.android)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Room: la versión viene del catálogo (libs.versions.toml)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Login con Google y Apple (Firebase Authentication + Credential Manager)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    // Permite usar .await() sobre las Task de Firebase
    implementation(libs.kotlinx.coroutines.play.services)

    // Guía 14: consumo de API REST (Retrofit + conversor Gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    // Cliente HTTP de Retrofit: se declara porque ApiSimulada usa su Interceptor.
    // Es la misma versión que ya llegaba como dependencia de Coil.
    implementation(libs.okhttp)
}
