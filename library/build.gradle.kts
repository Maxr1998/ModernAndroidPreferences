import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    id("de.mannodermaus.android-junit5")
    id("com.adarshr.test-logger") version "2.0.0"
    id("maven-publish")
    id("com.jfrog.bintray")
    id("com.github.ben-manes.versions") version "0.29.0"
}

// Versions
val libraryVersion = "0.5.8"
val libraryName = "modernpreferences"
val bintrayLibraryName = "ModernAndroidPreferences"

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = libraryVersion
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
    lintOptions {
        isAbortOnError = false
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Dependencies.Core.appCompat)
    implementation(Dependencies.Core.coreKtx)
    implementation(Dependencies.UI.constraintLayout)
    implementation(Dependencies.UI.recyclerView)

    // Testing
    testImplementation(Dependencies.Testing.junit5)
    testRuntimeOnly(Dependencies.Testing.junit5Engine)
    testImplementation(Dependencies.Testing.kotestAssertions)
    testImplementation(Dependencies.Testing.kotestProperty)
    testImplementation(Dependencies.Testing.kotestRunner)
    testImplementation(Dependencies.Testing.mockk)
    testImplementation(Dependencies.Testing.sharedPreferencesMock)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

var bintrayKey: String? = null
var githubToken: String? = null
val propFile = project.file("key.properties")
if (propFile.exists()) {
    val props = Properties()
    props.load(FileInputStream(propFile))
    bintrayKey = props["bintrayKey"] as? String
    githubToken = props["githubToken"] as? String
}

// Maven & Bintray config
val publicationName = "production"
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Maxr1998/ModernAndroidPreferences")
            credentials {
                username = "Maxr1998"
                password = githubToken
            }
        }
    }
    publications {
        register(publicationName, MavenPublication::class) {
            groupId = "de.Maxr1998.android"
            artifactId = libraryName
            version = libraryVersion
            artifact("$buildDir/outputs/aar/library-release.aar")
            artifact(sourcesJar.get())

            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                configurations.implementation.get().allDependencies.forEach {
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    dependencyNode.appendNode("groupId", it.group)
                    dependencyNode.appendNode("artifactId", it.name)
                    dependencyNode.appendNode("version", it.version)
                }
            }
        }
    }
}

bintray {
    user = "maxr1998"
    key = bintrayKey
    setPublications(publicationName)
    pkg.apply {
        repo = "maven"
        name = bintrayLibraryName
        version.name = libraryVersion
        publish = true
    }
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = GradleReleaseChannel.CURRENT.id
    rejectVersionIf {
        !Dependencies.Versions.isStable(candidate.version) && Dependencies.Versions.isStable(currentVersion)
    }
}