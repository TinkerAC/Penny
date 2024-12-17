package app.penny

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.optOutOfCupertinoOverscroll
import androidx.compose.runtime.ProvidedValue
import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinAppDeclaration
import platform.UIKit.UIDevice

class IOSPlatform : Platform() {
    override val name: String =
        UIDevice.currentDevice.systemName()

    override val version: String = UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform {
    println("getPlatform: ${IOSPlatform()}")
    return IOSPlatform()
}


actual class ApplicationInitializer actual constructor(
    application: Any?
) : KoinComponent {
    actual fun initKoin(appDeclaration: KoinAppDeclaration): ApplicationInitializer {
        app.penny.di.initKoin()
        return this
    }

}

actual fun provideNullAndroidOverscrollConfiguration(): Array<ProvidedValue<*>> {
    return emptyArray<ProvidedValue<*>>()
}

@OptIn(ExperimentalFoundationApi::class)
actual fun disableUiKitOverscroll() {
    optOutOfCupertinoOverscroll()

}