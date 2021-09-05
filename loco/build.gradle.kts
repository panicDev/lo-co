
plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

object Maven {
    const val groupId = "id.panic_dev.android"
    const val artifactId = "lo-co"
    const val name = "lo-co"
    const val version = "0.1.0"
    const val desc = "Coroutines function for FusesLocationProviderClient"
    const val siteUrl = "https://github.com/panicDev/loc-co"
    const val gitUrl = "https://github.com/panicDev/lo-co.git"
    const val licenseName = "The Apache Software License, Version 2.0"
    const val licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.txt"
    const val licenseDist = "repo"
}

group = Maven.groupId
version = Maven.version

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 16
        consumerProguardFiles("consumer-proguard-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    lint {
        textReport = true
        textOutput("stdout")
        isCheckDependencies = true
    }

    libraryVariants.all {
        generateBuildConfigProvider?.configure {
            enabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
            apiVersion = "1.5"
            languageVersion = "1.5"
        }
    }
}

dependencies {
    api(Kotlin.stdlib)
    api(KotlinX.coroutines.core)
    api(KotlinX.coroutines.android)
    api(KotlinX.coroutines.playServices)
    api(Google.android.playServices.location)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.panicDev"
                artifactId = "lo-co"
                version = "0.1.1"
            }

        }
    }
}