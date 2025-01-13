package app.penny.core.data.kvstore

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class UserPreferenceManager(
    private val settings: Settings
) {

    fun putString(key: String, value: String) {
        settings.set(key = key, value = value)
    }

    fun getStringOrNull(key: String) = settings.getStringOrNull(key)

    fun putBoolean(key: String, value: Boolean) {
        settings.set(key = key, value = value)
    }

    fun getBoolean(key: String) = settings.getBoolean(key, false)


    companion object {
        const val THEME_MODE = "theme_mode" //STATIC, DYNAMIC
        const val STATIC_THEME_NAME = "static_theme_name"
        const val DYNAMIC_THEME_SEED_COLOR_ARGB = "dynamic_theme_seed_color_argb"
        const val CONSTRAINTS = "constraints"//LOW, MEDIUM, HIGH
        const val DARK_MODE = "dark_mode" //SYSTEM, LIGHT, DARK
        const val LANGUAGE = "language" //ENGLISH, CHINESE
        const val NOTIFICATION_ENABLED = "notification_enabled"
        const val SCHEDULED_NOTIFICATION_ENABLED = "scheduled_notification_enabled"
        const val BUDGET_REACHED_NOTIFICATION_ENABLED = "budget_reached_notification_enabled"
        const val AUTO_CLOUD_SYNC_ENABLED = "auto_cloud_sync_enabled"

    }


}


