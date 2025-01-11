package app.penny.platform

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource


private lateinit var appContext: Context

/**
 * 初始化 Context。确保在 Application 或 Activity 中调用。
 */
fun initMokoResources(context: Context) {
    appContext = context
}

@OptIn(ExperimentalFoundationApi::class)
actual fun provideNullAndroidOverscrollConfiguration(): Array<ProvidedValue<*>> {
    return arrayOf(LocalOverscrollConfiguration provides null)
}

actual fun disableUiKitOverscroll() {
}

@Composable
actual fun getScreenWidthDp(): Dp = LocalConfiguration.current.screenWidthDp.dp

@Composable
actual fun getScreenHeightDp(): Dp = LocalConfiguration.current.screenHeightDp.dp


/**
 * 直接返回原始字符串资源的 API
 */
actual fun getRawStringResource(
    stringResource: StringResource,
    localeString: String
): String {
    // 将 String 转换为 Locale
    val locale = java.util.Locale(localeString)

    // 更新配置
    val config = Configuration(Resources.getSystem().configuration)
    config.setLocale(locale)

    val localizedContext = appContext.createConfigurationContext(config)
    return localizedContext.getString(stringResource.resourceId)
}
