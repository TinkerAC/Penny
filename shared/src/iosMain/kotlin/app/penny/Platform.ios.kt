package app.penny


//import com.mmk.kmpnotifier.notification.NotifierManager
//import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.optOutOfCupertinoOverscroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import dev.icerock.moko.resources.StringResource
import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinAppDeclaration
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.NSURL
import platform.Foundation.countryCode
import platform.Foundation.languageCode
import platform.UIKit.UIApplication
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

    actual fun initNotifierManager(): ApplicationInitializer {
        NotifierManager.initialize(
            NotificationPlatformConfiguration.Ios()
        )
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



//// CommonMain
//actual class LocalNotificationManager {
//    // 实时发送通知
//    actual fun sendImmediateNotification(title: String, body: String) {
//        val content = UNMutableNotificationContent().apply {
//            setTitle(title)
//            setBody(body)
//            setSound(UNNotificationSound.defaultSound())
//            setBadge(
//                NSNumber(
//                    UIApplication.sharedApplication.applicationIconBadgeNumber + 1
//                )
//            )
//        }
//
//        val request = UNNotificationRequest.requestWithIdentifier(
//            identifier = "immediate_notification",
//            content = content,
//            trigger = null // 立即触发
//        )
//
//        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(
//            request = request
//        ) { error ->
//            if (error != null) {
//                println("实时通知发送失败: ${error.localizedDescription}")
//            } else {
//                println("实时通知已发送")
//            }
//        }
//    }
//
//    // 设置定时通知
//    actual fun scheduleNotification(
//        title: String,
//        body: String,
//        delaySeconds: Long,
//        identifier: String
//    ) {
//        val content = UNMutableNotificationContent().apply {
//            setTitle(title)
//            setBody(body)
//            setSound(UNNotificationSound.defaultSound())
//            setBadge(
//                NSNumber(
//                    UIApplication.sharedApplication.applicationIconBadgeNumber + 1
//                )
//            )
//        }
//
//        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
//            timeInterval = delaySeconds.toDouble(),
//            repeats = false // 不重复
//        )
//
//        val request = UNNotificationRequest.requestWithIdentifier(
//            identifier = identifier,
//            content = content,
//            trigger = trigger
//        )
//
//        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(
//            request = request
//        ) { error ->
//            if (error != null) {
//                println("定时通知设置失败: ${error.localizedDescription}")
//            } else {
//                println("定时通知已设置: $identifier")
//            }
//        }
//    }
//}

