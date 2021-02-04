import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("de.mannodermaus.android-junit5")
    id("com.adarshr.test-logger") version Dependencies.Versions.testLogger
    `maven-publish`
    signing
    id("com.github.ben-manes.versions") version Dependencies.Versions.dependencyUpdates
}

// Versions
val libraryVersion = "2.0"
val libraryGroup = "de.maxr1998"
val libraryName = "modernandroidpreferences"
val prettyLibraryName = "ModernAndroidPreferences"

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
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
    implementation(Dependencies.Core.appCompat)
    implementation(Dependencies.Core.coreKtx)
    implementation(Dependencies.UI.constraintLayout)
    implementation(Dependencies.UI.recyclerView)

    // Testing
    testImplementation(Dependencies.Testing.junit)
    testRuntimeOnly(Dependencies.Testing.junitEngine)
    testImplementation(Dependencies.Testing.kotestAssertions)
    testImplementation(Dependencies.Testing.kotestProperty)
    testImplementation(Dependencies.Testing.kotestRunner)
    testImplementation(Dependencies.Testing.mockk)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

var ossrhUsername: String? = null
var ossrhPassword: String? = null
var githubToken: String? = null
val propFile = project.file("key.properties")
if (propFile.exists()) {
    val props = Properties()
    props.load(FileInputStream(propFile))
    ext["signing.gnupg.keyName"] = props["signingKeyName"] as? String
    ossrhUsername = props["ossrhUsername"] as? String
    ossrhPassword = props["ossrhPassword"] as? String
    githubToken = props["githubToken"] as? String
}

// Maven publishing config
publishing {
    publications {
        register("production", MavenPublication::class) {
            groupId = libraryGroup
            artifactId = libraryName
            version = android.defaultConfig.versionName
            artifact("$buildDir/outputs/aar/library-release.aar")
            artifact(sourcesJar.get())

            pom {
                name.set(prettyLibraryName)
                description.set("Android Preferences defined through Kotlin DSL, shown in a RecyclerView")
                url.set("https://github.com/Maxr1998/ModernAndroidPreferences")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("Maxr1998")
                        name.set("Max Rumpf")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/Maxr1998/ModernAndroidPreferences.git")
                    developerConnection.set("scm:git:ssh://github.com/Maxr1998/ModernAndroidPreferences.git")
                    url.set("https://github.com/Maxr1998/ModernAndroidPreferences/tree/master")
                }
                withXml {
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
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Maxr1998/ModernAndroidPreferences")
            credentials {
                username = "Maxr1998"
                password = githubToken
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = GradleReleaseChannel.CURRENT.id
    rejectVersionIf {
        !Dependencies.Versions.isStable(candidate.version) && Dependencies.Versions.isStable(currentVersion)
    }
}