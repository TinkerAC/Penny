package app.penny.core.data.kvstore

import kotlin.uuid.Uuid
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlin.uuid.ExperimentalUuidApi

class UserDataManager(
    private val settings: Settings
) {
    fun putString(key: String, value: String) {
        settings.set(key = key, value = value)
    }

    fun getNonFlowString(key: String) = settings.getString(key = key, defaultValue = "")

    fun putLong(key: String, value: Long) {
        settings.set(key = key, value = value)
    }

    fun getLong(key: String) = settings.getLong(key = key, defaultValue = 0)


    fun getLongOrNull(key: String): Long? {
        return settings.getLongOrNull(key)
    }


    fun getStringOrNull(key: String): String? {
        return settings.getStringOrNull(key)
    }

    @OptIn(ExperimentalUuidApi::class)
    fun generateUserUuid(): String {
        return Uuid.random().toString()
    }


    companion object {
        const val RECENT_LEDGER_UUID = "recent_ledger_uuid"
        const val CONTINUOUS_CHECK_IN_DAYS = "continuous_check_in_days"
        const val USER_UUID = "user_uuid"
        const val LAST_SYNCED_AT = "last_synced_at"
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
        const val IS_FIRST_TIME = "is_first_time"
    }


    fun putInt(key: String, value: Int) {
        settings.set(key = key, value = value)
    }

    fun getInt(key: String) = settings.getInt(key = key, defaultValue = 0)

    fun clear() {
        settings.clear()
    }


    fun getBooleanOrNull(key: String): Boolean? {
        return settings.getBooleanOrNull(key)
    }
    fun getBoolean(key: String): Boolean {
        return settings.getBoolean(key = key, defaultValue = false)
    }


    fun putBoolean(key: String, value: Boolean) {
        settings.set(key = key, value = value)
    }
}