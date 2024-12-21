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


    companion object {
        const val THEME_MODE = "theme_mode" //STATIC, DYNAMIC
        const val STATIC_THEME_NAME = "static_theme_name"
        const val DYNAMIC_THEME_SEED_COLOR_ARGB = "dynamic_theme_seed_color_argb"
        const val CONSTRAINTS = "constraints"//LOW, MEDIUM, HIGH
        const val DARK_MODE = "dark_mode" //SYSTEM, LIGHT, DARK
        const val LANGUAGE = "language" //ENGLISH, CHINESE
    }


}


