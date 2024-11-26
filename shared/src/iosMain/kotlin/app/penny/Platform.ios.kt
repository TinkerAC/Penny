package app.penny

import org.koin.dsl.KoinAppDeclaration
import platform.UIKit.UIDevice
import org.koin.core.component.KoinComponent

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()


actual class ApplicationInitializer actual constructor(
     application: Any?
) : KoinComponent {
    actual fun initKoin(appDeclaration: KoinAppDeclaration): ApplicationInitializer {
        app.penny.di.initKoin()
        return this
    }

}