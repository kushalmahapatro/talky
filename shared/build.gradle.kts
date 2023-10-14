val ktorVersion = "2.3.5"
val decomposeVersion = "2.1.2"
val essentyVersion = "1.2.0"
val precompose_version = "1.5.4"

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.0"
    id("com.android.library")
    id("org.jetbrains.compose")
    id("kotlin-parcelize")
}



kotlin {
    androidTarget()

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true

            export("com.arkivanov.decompose:decompose:$decomposeVersion-compose-experimental")
            export("com.arkivanov.decompose:decompose:$decomposeVersion")
            export("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion-compose-experimental")
            export("com.arkivanov.essenty:lifecycle:$essentyVersion")
        }
    }




    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

                // ktor
                api("io.ktor:ktor-client-core:$ktorVersion")
                api("io.ktor:ktor-client-cio:$ktorVersion")
                api("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                api("io.ktor:ktor-client-logging:$ktorVersion")

                //moko
                api("dev.icerock.moko:mvvm-core:0.16.1")
                api("dev.icerock.moko:mvvm-compose:0.16.1")

                // decompose
//                api("com.arkivanov.decompose:decompose:$decomposeVersion-compose-experimental")
                api("com.arkivanov.decompose:decompose:$decomposeVersion")
                api("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion-compose-experimental")
                api("com.arkivanov.essenty:lifecycle:$essentyVersion")



            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.8.0")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.12.0")
                // ktor
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                api("com.arkivanov.decompose:decompose:$decomposeVersion")

                api("com.arkivanov.essenty:lifecycle:1.2.0")

            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                // ktor
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
//                api("com.arkivanov.decompose:decompose:$decomposeVersion-compose-experimental")
                api("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion-compose-experimental")
                api("com.arkivanov.essenty:lifecycle:1.2.0")

                api("moe.tlaster:precompose:$precompose_version")



            }

        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation(compose.materialIconsExtended)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
                // ktor
                implementation("io.ktor:ktor-client-core:$ktorVersion")

            }
        }

    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.talky.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildToolsVersion = "34.0.0"
    ndkVersion = "26.0.10792818"
    kotlin {
        jvmToolchain(17)
    }
}
dependencies {
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.5.3")
}

