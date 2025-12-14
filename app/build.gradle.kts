import java.util.Properties

// --- Plugins ---
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    //id("org.sonarqube") version "5.0.0.4638" // Plugin de SonarQube
}


android {
    namespace = "com.company.ulpgcflix"
    compileSdk = 36
    buildFeatures {
        buildConfig = true
        compose = true
    }

    defaultConfig {
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { properties.load(it) }
        } else {
            println("WARNING: local.properties file not found.")
        }
        val tmdbApiKey: String = properties.getProperty("TMDB_API_KEY") ?: "DEFAULT_API_KEY_OR_ERROR"
        // --- BuildConfig Field ---
        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")

        applicationId = "com.company.ulpgcflix"
        minSdk = 24
        targetSdk = 36 // Usamos la misma que compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    // Configuración para usar la carpeta 'src/main/java' o 'src/main/kotlin' como fuente
    sourceSets.getByName("main") {
        java.srcDirs("src/main/java", "src/main/kotlin")
    }
}

// --- Dependencias ---
dependencies {
    // Es recomendable usar la versión más reciente del BOM para estabilidad
    implementation(platform("com.google.firebase:firebase-bom:33.1.0")) // Usando una versión estable
    implementation("com.google.firebase:firebase-auth-ktx") // Versiones sin especificar ya que están en el BOM
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt:coil-compose:2.6.0")
}


