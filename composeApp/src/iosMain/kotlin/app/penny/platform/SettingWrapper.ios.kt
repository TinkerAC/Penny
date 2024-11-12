package app.penny.platform

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings


actual class MultiplatformSettingsWrapper {
    actual fun createSettings(): Settings {
        val delegate = NSUserDefaults.standardUserDefaults
        return NSUserDefaultsSettings(delegate)
    }
}