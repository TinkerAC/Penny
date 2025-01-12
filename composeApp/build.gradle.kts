import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {

    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
    id("dev.icerock.mobile.multiplatform-resources") version "0.24.4"
    kotlin("native.cocoapods") version "2.1.0"


}


kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }


    jvm("desktop")

    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())

    cocoapods {
        // Required properties
        // Specify the required Pod version here. Otherwise, the Gradle project version is used.
        version = "1.16.2"
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"

        // Optional properties
        // Configure the Pod name here instead of changing the Gradle project name
        name = "ComposeAppCocoaPod"

        framework {
            // Required properties
            // Framework name configuration. Use this property instead of deprecated 'frameworkName'
            baseName = "ComposeApp"

            // Optional properties
            // Specify the framework linking type. It's dynamic by default.
            isStatic = true
            // Dependency export
            // Uncomment and specify another project module if you have one:
            export(projects.shared)
//            export(libs.kmpnotifier)
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            transitiveExport = false // This is default.

        }

        // Maps custom Xcode configuration to NativeBuildType
        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                // 公共依赖项
                api(projects.shared)
//                implementation(libs.composable.table)
                implementation(libs.koin.core)
                api(libs.multiplatformSettings.noArg)
                api(libs.multiplatformSettings.coroutines)
                implementation(libs.bignum)
                implementation(libs.org.jetbrains.kotlin.kotlin.stdlib)
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
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.thechance101.chart)
                implementation(libs.kermit)
                implementation(compose.materialIconsExtended)
//                implementation(libs.record.core)

            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.android)
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.android.driver)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.sqlite.driver)
                implementation(libs.jdbc.driver)
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.ui.tooling.preview.desktop)
            }
        }


    }

}
android {
    namespace = "app.penny.composeApp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "app.penny.composeApp"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
            modules("java.sql")
            packageName = "Penny"
            packageVersion = "1.0.0"

            macOS {
                iconFile.set(file("../images/icon.icns"))
            }
            windows {
                iconFile.set(file("../images/icon.ico"))
            }
            linux {
                iconFile.set(file("../images/icon.png"))
            }
        }
    }
}

multiplatformResources {
    resourcesClassName = "SharedRes"
    resourcesPackage = "app.penny.shared"
    iosBaseLocalizationRegion = "en"
}
