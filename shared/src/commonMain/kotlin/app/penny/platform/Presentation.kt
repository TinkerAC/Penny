package app.penny.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.unit.Dp
import app.penny.presentation.ui.LocaleManager
import dev.icerock.moko.resources.StringResource

expect fun disableUiKitOverscroll()

expect fun provideNullAndroidOverscrollConfiguration(): Array<ProvidedValue<*>>


@Composable
expect fun getScreenWidthDp(): Dp

@Composable
expect fun getScreenHeightDp(): Dp

/**
 * 直接返回原始字符串资源的 API
 */
expect fun getRawStringResource(
    stringResource: StringResource, localeString: String = LocaleManager.currentLocale
): String
