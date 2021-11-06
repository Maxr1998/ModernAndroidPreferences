import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel

// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.dependencyupdates)
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }

    // Configure dependency updates task
    withType<DependencyUpdatesTask> {
        gradleReleaseChannel = GradleReleaseChannel.CURRENT.id
        rejectVersionIf {
            val currentType = classifyVersion(currentVersion)
            val candidateType = classifyVersion(candidate.version)

            when (candidateType) {
                // Always accept stable updates
                VersionType.STABLE -> true
                // Accept milestone updates for current milestone and unstable
                VersionType.MILESTONE -> currentType != VersionType.STABLE
                // Only accept unstable for current unstable
                VersionType.UNSTABLE -> currentType == VersionType.UNSTABLE
            }.not()
        }
    }

    create<Delete>("clean") {
        delete(rootProject.buildDir)
    }
}