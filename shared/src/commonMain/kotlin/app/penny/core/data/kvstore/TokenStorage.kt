package app.penny.core.data.kvstore

import com.russhwolf.settings.Settings

class TokenStorage(
    private val settings: Settings
) {


    private var accessToken: String? = null

    companion object {
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
    }

    fun saveAccessToken(token: String) {
        accessToken = token
    }

    fun getAccessToken(): String? {
        return accessToken
    }

    fun clearAccessToken() {
        accessToken = null
    }


    fun saveRefreshToken(token: String) {
        settings.putString(REFRESH_TOKEN, token)
    }

    fun getRefreshToken(): String? {
        return settings.getStringOrNull(REFRESH_TOKEN)
    }

    fun clearRefreshToken() {
        settings.remove(REFRESH_TOKEN)
    }


}