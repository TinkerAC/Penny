package app.penny.di


import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import org.koin.core.context.startKoin
import org.koin.core.error.KoinAppAlreadyStartedException

fun initKoin() {
    println("initKoin called ")
    // can't figure out how to get this to work without the try/catch on iOS
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
    } catch (
        e: KoinAppAlreadyStartedException
    ) {
        println("initKoin error: $e")
    }


}

