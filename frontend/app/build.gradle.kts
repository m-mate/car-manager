plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
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


}