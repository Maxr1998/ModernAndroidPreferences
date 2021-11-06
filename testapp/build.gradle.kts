plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "de.Maxr1998.modernpreferences.example"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    lint {
        isAbortOnError = false
    }
}

dependencies {
    implementation(Dependencies.Core.appCompat)
    implementation(Dependencies.Core.coreKtx)
    implementation(Dependencies.Core.activityKtx)
    implementation(Dependencies.LifecycleX.viewModel)
    implementation(Dependencies.LifecycleX.runtime)
    implementation(Dependencies.LifecycleX.common)
    implementation(Dependencies.UI.recyclerView)

    // Preferences library
    implementation(project(":library"))

    debugImplementation(Dependencies.Testing.leakCanary)
}