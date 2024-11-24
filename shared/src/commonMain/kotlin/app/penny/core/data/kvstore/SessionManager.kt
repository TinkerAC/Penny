package app.penny.core.data.kvstore

import com.russhwolf.settings.Settings

class SessionManager(
    private val settings: Settings
) {

    companion object {
        private const val TOKEN_KEY = "user_token"
    }

    fun saveToken(token: String) {
        settings.putString(TOKEN_KEY, token)
    }

    fun getToken(): String? {
        return settings.getStringOrNull(TOKEN_KEY)
    }

    fun clearToken() {
        settings.remove(TOKEN_KEY)
    }


}