// file: shared/src/commonMain/kotlin/app/penny/di/initKoin.kt
package app.penny.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import org.koin.core.context.startKoin
import org.koin.core.error.KoinAppAlreadyStartedException

fun initKoin() {
    println("initKoin called")
    try {
        startKoin {
            logger(
                KermitKoinLogger(Logger.withTag("koin"))
            )
            modules(
                listOf(
                    platformModule(),
                    commonModule()
                )
            )
        }
    } catch (e: KoinAppAlreadyStartedException) {
        println("initKoin error: ${e.message}")
        // 打印堆栈跟踪
        e.printStackTrace()

//        // 或者获取 Kotlin/Native 堆栈信息
//        val stackTrace = e.stackTrace.joinToString("\n")
//        println("Stack Trace:\n$stackTrace")
    } catch (e: Exception) {
        println("initKoin unexpected error: ${e.message}")
        e.printStackTrace()
//
//        val stackTrace = e.stackTrace.joinToString("\n")
//        println("Stack Trace:\n$stackTrace")
    }
}
