package app.penny.platform

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.optOutOfCupertinoOverscroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.languageCode
import platform.UIKit.UIScreen

/**
 * 根据 StringResource 和指定语言环境（localeString）获取字符串资源
 */
actual fun getRawStringResource(
    stringResource: StringResource,
    localeString: String
): String {
    // 将 localeString 转换为 NSLocale 格式
    val nsLocale = createNSLocaleFromString(localeString)

    // 从指定语言环境加载对应的 .strings 文件
    val localizedBundle = createLocalizedBundle(stringResource.bundle, nsLocale)
        ?: return "Unknown Key" // 如果找不到对应的语言包，返回占位值

    // 从本地化的 bundle 中获取字符串
    return localizedBundle.localizedStringForKey(stringResource.resourceId, "", null)
}

/**
 * 辅助函数：将 localeString 转换为 NSLocale 对象
 */
private fun createNSLocaleFromString(localeString: String): NSLocale {
    val localeIdentifier = localeString.replace("-", "_")
    return NSLocale(localeIdentifier)
}

/**
 * 辅助函数：根据 NSLocale 查找对应语言的 NSBundle
 */
private fun createLocalizedBundle(baseBundle: NSBundle, locale: NSLocale): NSBundle? {
    val languageCode = locale.languageCode ?: return null
    val regionCode = locale.countryCode

    // 尝试构建语言和区域目录
    val path = if (regionCode != null) {
        baseBundle.pathForResource("$languageCode-$regionCode", "lproj")
    } else {
        baseBundle.pathForResource(languageCode, "lproj")
    }

    // 如果路径存在，返回对应的 bundle，否则返回 null
    return if (path != null) NSBundle(path = path) else null
}

fun Int.pxToPoint(): Double = this.toDouble() / UIScreen.mainScreen.scale


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
