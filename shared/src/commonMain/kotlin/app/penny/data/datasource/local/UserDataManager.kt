package app.penny.data.datasource.local

import kotlin.uuid.Uuid
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlin.uuid.ExperimentalUuidApi

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


    @OptIn(ExperimentalUuidApi::class)
    fun generateUserUuid(): String {
        return Uuid.random().toString()
    }



    companion object {
        const val RECENT_LEDGER_ID = "recent_ledger_id"
        const val CONTINUOUS_CHECK_IN_DAYS = "continuous_check_in_days"
        const val USER_UUID = "user_uuid"
        const val LAST_SYNCED_AT = "last_synced_at"
    }


    fun putInt(key: String, value: Int) {
        settings.set(key = key, value = value)
    }

    fun getInt(key: String) = settings.getInt(key = key, defaultValue = 0)


}