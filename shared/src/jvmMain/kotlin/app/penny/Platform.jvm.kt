package app.penny

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinAppDeclaration
import java.util.Locale

class JVMPlatform() : Platform() {
    override val name: String = "Java"
    override val version: String = System.getProperty("java.version")
}


actual fun getPlatform(): Platform {
    return JVMPlatform()
}

actual class ApplicationInitializer actual constructor(
    val application: Any?
) : KoinComponent {


    actual fun initKoin(
        appDeclaration: KoinAppDeclaration
    ): ApplicationInitializer {
        app.penny.di.initKoin()
        return this
    }

}

actual fun provideNullAndroidOverscrollConfiguration(): Array<ProvidedValue<*>> {
    return emptyArray<ProvidedValue<*>>()
}

actual fun disableUiKitOverscroll() {
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenWidthDp(): Dp {
    return LocalWindowInfo.current.containerSize.width.dp
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenHeightDp(): Dp {
    return LocalWindowInfo.current.containerSize.height.dp
}

actual fun getRawStringResource(
    stringResource: StringResource,
    localeString: String
): String {
    val locale = Locale(localeString)
    return stringResource.localized(locale)
}
