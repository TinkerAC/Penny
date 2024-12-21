package app.penny



import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.optOutOfCupertinoOverscroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinAppDeclaration
import platform.UIKit.UIDevice
import platform.UIKit.UIScreen

class IOSPlatform : Platform() {
    override val name: String =
        UIDevice.currentDevice.systemName()

    override val version: String = UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform {
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenWidthDp(): Dp = LocalWindowInfo.current.containerSize.width.pxToPoint().dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenHeightDp(): Dp = LocalWindowInfo.current.containerSize.height.pxToPoint().dp

fun Int.pxToPoint(): Double = this.toDouble() / UIScreen.mainScreen.scale


