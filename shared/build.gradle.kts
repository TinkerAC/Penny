import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.kotlinMultiplatform)
//    alias(libs.plugins.androidApplication)
    alias(libs.plugins.androidLibrary)

    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
    id("dev.icerock.mobile.multiplatform-resources") version "0.24.4"

}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }



    jvm {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += listOf("-Xdisable-optimizations")
            }
        }
    }


    // 新的 iOS 目标配置方式


    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())
    iosTargets.forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "ComposeApp"
//            export(libs.kmpnotifier)
//            isStatic = true
//        }
    }

    // 应用默认的层次结构模板
    applyDefaultHierarchyTemplate()



    sourceSets {
        val commonMain by getting {

            dependencies {
                // 公共依赖项
                implementation(libs.uuid)

                api(libs.kmpnotifier)
                api(libs.resources.compose) // 如果使用 Compose Multiplatform
                implementation(libs.material.kolor)
                implementation(libs.colorpicker.compose)
                implementation(libs.multiplatform.markdown.renderer.m3)
//                implementation(libs.kamel.image.default)
                implementation(libs.ktor.utils)
                api(libs.multiplatformSettings.noArg)
                api(libs.multiplatformSettings.coroutines)
                implementation(libs.koin.core)
//                implementation(libs.kmp.io)
                implementation(libs.okio)
                implementation(libs.coil.compose)
//                implementation(libs.coil.network.okhttp)


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
                api(libs.koin.compose)
//                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
//                implementation(libs.coil.svg)
                implementation(libs.voyager.lifecycle.kmp)
                implementation(libs.coil3.coil.svg)
                implementation(libs.coil.compose)


//                implementation(libs.thechance101.chart)
                implementation(libs.kermit)
                implementation(libs.kermit.koin)
                implementation(compose.materialIconsExtended)
                implementation(libs.ktor.client.cio)


                //image loader
                implementation(libs.coil.compose)

                implementation(libs.multiplatform.paths)

                implementation(libs.coil.network.ktor)


            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.android)
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.android.driver)
                implementation(libs.koin.android)


                //okhttp

                implementation(libs.ktor.client.okhttp)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.sqlite.driver)
                implementation(libs.jdbc.driver)
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.ui.tooling.preview.desktop)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.native.driver)
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

android {
    namespace = "app.penny.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
//        applicationId = "app.penny"
        minSdk = libs.versions.android.minSdk.get().toInt()
//        targetSdk = libs.versions.android.targetSdk.get().toInt()
//        versionCode = 1
//        versionName = "1.0"
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
    implementation(libs.androidx.ui.tooling.preview.desktop)
    debugImplementation(compose.uiTooling)
}


sqldelight {
    databases {
        create("PennyDatabase") {
            packageName.set("app.penny.database")
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:2.0.2")
        }
    }
}
multiplatformResources {

    resourcesClassName = "SharedRes"
    resourcesPackage = "app.penny.shared"
    iosBaseLocalizationRegion = "en"
}

