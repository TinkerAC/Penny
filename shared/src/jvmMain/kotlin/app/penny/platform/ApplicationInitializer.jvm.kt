package app.penny.platform

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinAppDeclaration

actual class ApplicationInitializer actual constructor(
    val application: Any?
) : KoinComponent {


    actual fun initKoin(
        appDeclaration: KoinAppDeclaration
    ): ApplicationInitializer {
        app.penny.di.initKoin()
        return this
    }

    actual fun initNotifierManager(): ApplicationInitializer {
        NotifierManager.initialize(
            NotificationPlatformConfiguration.Desktop()
        )
        return this
    }

}