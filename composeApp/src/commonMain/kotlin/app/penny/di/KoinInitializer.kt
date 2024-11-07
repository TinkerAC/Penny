package app.penny.di


import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import org.koin.core.context.startKoin

fun initKoin() {
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

}

