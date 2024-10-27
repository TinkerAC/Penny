package app.penny.di


import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

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

fun initKoinAndroid(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        modules(
            listOf(
                platformModule(),
                commonModule()
            )
        )
        appDeclaration()
    }
}
