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
        const val THEME_NAME = "theme_name"
        const val CONSTRAINTS = "constraints"//LOW, MEDIUM, HIGH
        const val DARK_MODE = "dark_mode" //SYSTEM, LIGHT, DARK
    }


}


