import java.util.*

object Dependencies {
    object Versions {
        // Plugins
        const val detekt = "1.16.0"
        const val testLogger = "2.1.1"
        const val dependencyUpdates = "0.36.0"

        // Core
        const val appCompat = "1.2.0"
        const val coreKtx = "1.3.2"
        const val activityKtx = "1.1.0"

        // UI
        const val constraintLayout = "2.0.0-beta6"
        const val recyclerView = "1.1.0"
        const val googleMaterial = "1.5.0-alpha04"

        // Lifecycle
        const val lifecycleExtensions = "2.2.0"

        // Testing
        const val junit = "5.7.0"
        const val kotest = "4.4.0"
        const val mockk = "1.10.5"

        // Debug
        const val leakCanary = "2.5"

        fun isStable(version: String): Boolean {
            return listOf("alpha", "beta", "dev", "rc", "m").none {
                version.toLowerCase(Locale.ROOT).contains(it)
            }
        }
    }

    object Core {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
        const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        const val activityKtx = "androidx.activity:activity-ktx:${Versions.activityKtx}"
    }

    object UI {
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
        const val googleMaterial = "com.google.android.material:material:${Versions.googleMaterial}"
    }

    object LifecycleX {
        val viewModel = lifecycleX("viewmodel-ktx")
        val runtime = lifecycleX("runtime-ktx")
        val common = lifecycleX("common-java8")
    }

    /**
     * Includes logging, debugging, and testing
     */
    object Testing {
        const val junit = "org.junit.jupiter:junit-jupiter-api:${Versions.junit}"
        const val junitEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit}"
        const val kotestAssertions = "io.kotest:kotest-assertions-core-jvm:${Versions.kotest}"
        const val kotestProperty = "io.kotest:kotest-property-jvm:${Versions.kotest}"
        const val kotestRunner = "io.kotest:kotest-runner-junit5-jvm:${Versions.kotest}"
        const val mockk = "io.mockk:mockk:${Versions.mockk}"

        // Debug
        const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
    }

    // Helpers
    private fun lifecycleX(module: String) =
        "androidx.lifecycle:lifecycle-$module:${Versions.lifecycleExtensions}"
}