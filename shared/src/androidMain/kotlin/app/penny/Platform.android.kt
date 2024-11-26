package app.penny

import android.app.Application
import android.os.Build
import app.penny.di.commonModule
import app.penny.di.platformModule
import co.touchlab.kermit.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual class ApplicationInitializer actual constructor(
    val application: Any?
) : KoinComponent {
    private val logger = Logger.withTag("ApplicationInitializer")

    actual fun initKoin(appDeclaration: KoinAppDeclaration): ApplicationInitializer {
        logger.i { "Initializing Koin..." }
        startKoin {
            androidContext(androidContext = application as Application)
            modules(
                listOf(
                    platformModule(),
                    commonModule()
                )
            )
            appDeclaration()
        }
        logger.i { "Koin initialized successfully" }

        return this
    }

}