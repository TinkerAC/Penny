import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate() // this one


    sourceSets {
        val commonMain by getting {
            dependencies {
                // Koin 核心依赖，统一版本为 3.5.0
                implementation(libs.koin.core)
                // 其他依赖
                implementation(libs.bignum)
                implementation(libs.org.jetbrains.kotlin.kotlin.stdlib)
                implementation(libs.voyager.koin)
                implementation(libs.cafe.voyager.screenmodel)
                implementation(libs.runtime)
                implementation(libs.voyager.tab.navigator)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.transitions)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.datetime)
                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
            }
        }

        val androidMain by getting {
            dependencies {

                implementation(libs.ktor.client.android)
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.android.driver)
                // Koin Android 依赖，版本统一为 3.5.0
                implementation(libs.koin.android)
                implementation(libs.koin.android.ext)
                // 如果需要 Koin Core 扩展
                implementation(libs.koin.core.ext)
            }
        }

        val desktopMain by getting {
            dependencies {
                // Koin 核心依赖，版本统一为 3.5.0
                implementation(libs.koin.core)
                // 其他依赖
                implementation(libs.sqlite.driver)
                implementation(libs.jdbc.driver)
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.native.driver)
                // 其他依赖
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

android {
    namespace = "app.penny"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "app.penny"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "app.penny.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "app.penny"
            packageVersion = "1.0.0"
        }
    }
}

sqldelight {
    databases {
        create("PennyDatabase") {
            packageName.set("app.penny.database")
        }
    }
}
