plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

android {
    namespace = "hu.ait.weatherficks"
    compileSdk = 34

    defaultConfig {
        applicationId = "hu.ait.weatherficks"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Retrofit
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0") // JSON Parsing
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1") // JSON Parsing
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("io.coil-kt:coil-compose:2.4.0") // not used in this demo
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0") // Not mandatory (for plain text responses)
    // End Retrofit

    // Navigation
    val navVersion = "2.7.2"
    implementation("androidx.navigation:navigation-runtime-ktx:$navVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Hilt - DI
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")


    val roomVersion = "2.5.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    // Annotation processor
    ksp("androidx.room:room-compiler:$roomVersion")
    // Kotlin extension plugin for coroutine support for room
    implementation("androidx.room:room-ktx:$roomVersion")


    // Location Permissions
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.accompanist:accompanist-permissions:0.33.2-alpha")


    implementation("com.patrykandpatrick.vico:compose:1.13.0")

    // For `compose`. Creates a `ChartStyle` based on an M2 Material Theme.
    implementation("com.patrykandpatrick.vico:compose-m2:1.13.0")

    // For `compose`. Creates a `ChartStyle` based on an M3 Material Theme.
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.0")

    // Houses the core logic for charts and other elements. Included in all other modules.
    implementation("com.patrykandpatrick.vico:core:1.13.0")

    // For the view system.
    implementation("com.patrykandpatrick.vico:views:1.13.0")

}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}