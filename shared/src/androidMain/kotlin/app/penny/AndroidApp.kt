package app.penny

import android.app.Application
import app.penny.di.commonModule
import app.penny.di.platformModule
import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.i("AndroidApp.onCreate")


        startKoin {
            logger(
                KermitKoinLogger(Logger.withTag("koin"))
            )
            androidContext(this@AndroidApp)
            modules(
                listOf(
                    platformModule(),
                    commonModule()

                )
            )
        }


    }
}