package app.penny.data.datasource.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class UserDataManager(
    private val settings: Settings
) {
    fun setString(key: String, value: String) {
        settings.set(key = key, value = value)
    }

    fun getNonFlowString(key: String) = settings.getString(
        key = key,
        defaultValue = "",
    )

    fun putLong(key: String, value: Long) {
        settings.set(key = key, value = value)
    }

    fun getLong(key: String) = settings.getLong(key = key, defaultValue = 0)

    companion object {
        const val RECENT_LEDGER_ID = "recent_ledger_id"
    }


}