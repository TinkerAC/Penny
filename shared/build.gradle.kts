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

}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm()

    // 新的 iOS 目标配置方式

    iosX64 { binaries.framework { baseName = "shared" } }
    iosArm64 { binaries.framework { baseName = "shared" } }
    iosSimulatorArm64 { binaries.framework { baseName = "shared" } }
    // 应用默认的层次结构模板
    applyDefaultHierarchyTemplate()



    sourceSets {
        val commonMain by getting {

            dependencies {
                // 公共依赖项
//                implementation(libs.composable.table)
                implementation(libs.ktor.utils)
                api(libs.multiplatformSettings.noArg)
                api(libs.multiplatformSettings.coroutines)
                implementation(libs.koin.core)
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
                implementation(libs.thechance101.chart)
                implementation(libs.kermit)
                implementation(libs.kermit.koin)
                implementation(compose.materialIconsExtended)

                //键值对存储
//                implementation(libs.androidx.datastore.preferences)
//                implementation(libs.kotlinx.serialization.json) // 用于序列化


//                // 用于生成 UUID
//                implementation(libs.uuid)

                implementation(libs.ktor.client.cio)


                //image loader
                implementation(libs.coil.compose)
//                implementation(libs.coil.network.okhttp)




            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.android)
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.android.driver)
                implementation(libs.koin.android)
                implementation(libs.koin.android.ext)
                implementation(libs.koin.core.ext)

                //okhttp
                implementation(libs.ktor.client.okhttp)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.koin.core)
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
    namespace = "app.penny"
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
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:2.0.2")

            // 设置 SQLite 方言
        }
    }
}