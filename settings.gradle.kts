enableFeaturePreview("VERSION_CATALOGS")

include(":library", ":testapp")

rootProject.name = "ModernAndroidPreferences"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}