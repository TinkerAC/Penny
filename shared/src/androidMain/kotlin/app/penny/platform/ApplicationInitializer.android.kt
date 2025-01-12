package app.penny.platform

import android.app.Application
import app.penny.shared.R
import app.penny.di.commonModule
import app.penny.di.platformModule
import co.touchlab.kermit.Logger
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

actual class ApplicationInitializer actual constructor(
    val application: Any?,

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

    actual fun initNotifierManager(): ApplicationInitializer {
        logger.i { "Initializing NotifierManager..." }
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_notification
            )

        )
        logger.i { "NotifierManager initialized successfully" }

        return this
    }


}