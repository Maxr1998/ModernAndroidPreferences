object Dependencies {
    object Versions {
        // Plugins
        const val detekt = "1.18.1"

        // Core
        const val appCompat = "1.3.1"
        const val coreKtx = "1.7.0"
        const val activityKtx = "1.4.0"

        // UI
        const val constraintLayout = "2.1.1"
        const val recyclerView = "1.2.1"

        // Lifecycle
        const val lifecycleExtensions = "2.4.0"

        // Testing
        const val junit = "5.8.1"
        const val kotest = "4.6.3"
        const val mockk = "1.12.0"

        // Debug
        const val leakCanary = "2.7"
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