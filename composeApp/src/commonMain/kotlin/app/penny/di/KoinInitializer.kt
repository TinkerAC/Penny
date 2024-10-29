package app.penny.di


import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            listOf(
                platformModule(),
                commonModule()
            )
        )
    }

}

