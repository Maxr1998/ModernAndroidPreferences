import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.github.ben-manes.versions") version Dependencies.Versions.dependencyUpdates
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

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = GradleReleaseChannel.CURRENT.id
    rejectVersionIf {
        !Dependencies.Versions.isStable(candidate.version) && Dependencies.Versions.isStable(currentVersion)
    }
}