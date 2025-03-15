plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    //alias(libs.plugins.hilt.android) apply false// ✅ Make sure you have this in libs.versions.toml
    kotlin("kapt") // ✅ Add KAPT for annotation processing
}

hilt {
    enableAggregatingTask = false
}



android {
    namespace = "com.example.frontend"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.frontend"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14" // Replace with the latest version
    }
}

dependencies {

    // Core Compose dependencies
    implementation("com.github.yamin8000.gauge:Gauge:1.0.4")
    implementation("androidx.compose.ui:ui:1.5.14")
    implementation("androidx.compose.material:material:1.5.14")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.14")
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.foundation.android)
    implementation("io.github.ehsannarmani:compose-charts:0.1.1")
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.viewbinding)
    implementation(libs.core.ktx)
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.14")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.7.2")

    // Navigation for Compose (optional, if you want to use it)
    implementation("androidx.navigation:navigation-compose:2.7.2")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    val lifecycleversion = "1.6.1"
    implementation(libs.speedviewlib)
    val retrofitversion = "2.9.0"
    implementation (libs.retrofit)
    implementation(libs.converter.gson)
    val coroutinversion = "1.6.0"
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)
    //implementation(libs.hilt.android)
    //kapt(libs.hilt.compiler)
    //implementation(libs.hilt.navigation.compose)
    implementation("com.google.dagger:hilt-android:2.48") // ✅ Use latest version
    kapt("com.google.dagger:hilt-compiler:2.48")

    // Hilt for Jetpack Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // ✅ Ensure latest version

    // JavaPoet (used by Hilt internally)
    implementation("com.squareup:javapoet:1.13.0") // ✅ Use latest s

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.11.0") // Latest Mockito
    testImplementation("org.mockito:mockito-inline:4.11.0") // Enable inline mocking
    testImplementation("net.bytebuddy:byte-buddy:1.14.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")

}