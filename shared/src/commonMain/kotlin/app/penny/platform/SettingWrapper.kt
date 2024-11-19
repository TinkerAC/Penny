package app.penny.platform

import com.russhwolf.settings.Settings

expect class MultiplatformSettingsWrapper {
    fun createSettings(): Settings
}
