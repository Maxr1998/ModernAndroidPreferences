// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.kotlin.android) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}