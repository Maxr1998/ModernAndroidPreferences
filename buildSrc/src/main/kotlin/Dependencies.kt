import java.util.*

object Dependencies {
    object Versions {
        // Core
        const val appCompat = "1.1.0"
        const val coreKtx = "1.2.0"
        const val activityKtx = "1.1.0"

        // UI
        const val constraintLayout = "2.0.0-beta4"
        const val recyclerView = "1.1.0"

        // Lifecycle
        const val lifecycleExtensions = "2.2.0"

        // Testing
        const val junit = "4.13"
        const val androidXRunner = "1.2.0"
        const val androidXEspresso = "3.2.0"

        fun isStable(version: String): Boolean {
            return listOf("alpha", "beta", "dev", "rc").none { version.toLowerCase(Locale.ROOT).contains(it) }
        }
    }

    object Core {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
        const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        const val activityKtx = "androidx.activity:activity-ktx:${Versions.activityKtx}"
    }

    object UI {
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
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
        const val junit = "junit:junit:${Versions.junit}"
        const val androidXRunner = "androidx.test:runner:${Versions.androidXRunner}"
        const val androidXEspresso = "androidx.test.espresso:espresso-core:${Versions.androidXEspresso}"
    }

    // Helpers
    private fun lifecycleX(module: String) = "androidx.lifecycle:lifecycle-$module:${Versions.lifecycleExtensions}"
}