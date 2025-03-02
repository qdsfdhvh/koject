plugins {
    alias(libs.plugins.android.library)
    kotlin("android")
    alias(libs.plugins.publish)
    alias(libs.plugins.dokka)
}

android {
    namespace = "com.moriatsushi.koject.android.fragment"
    compileSdk = 33
    defaultConfig {
        minSdk = 14
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    api(project(":core"))
    api(project(":android:viewmodel"))
    implementation(libs.androidx.fragment)
}

tasks.dokkaHtmlPartial {
    moduleName.set("koject-android-fragment")
}
