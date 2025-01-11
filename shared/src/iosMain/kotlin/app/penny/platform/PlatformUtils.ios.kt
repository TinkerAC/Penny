package app.penny.platform

import platform.UIKit.UIDevice

class IOSPlatform : Platform() {
    override val name: String =
        UIDevice.currentDevice.systemName()

    override val version: String = UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform {
    return IOSPlatform()
}

