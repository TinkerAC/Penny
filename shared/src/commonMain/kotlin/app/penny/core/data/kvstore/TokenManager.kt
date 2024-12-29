// file: shared/src/commonMain/kotlin/app/penny/core/data/kvstore/TokenManager.kt
package app.penny.core.data.kvstore

import app.penny.core.network.clients.AuthApiClient
import app.penny.core.utils.JwtUtils
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings

interface TokenProvider {
    suspend fun getAccessToken(): String?
}

class TokenManager(
    private val settings: Settings,
    private val authApiClient: AuthApiClient
) : TokenProvider {

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    // 内存中的 Access Token
    private var accessToken: String? = null

    // 保存 Access Token 到内存
    fun saveAccessToken(token: String) {
        accessToken = token
        Logger.d { "Access Token saved to memory" }
    }

    // 从内存中获取 Access Token
    fun getAccessTokenFromMemory(): String? {
        return accessToken
    }

    // 清除内存中的 Access Token
    private fun clearAccessToken() {
        accessToken = null
        Logger.d { "Access Token cleared from memory" }
    }

    // 保存 Refresh Token 到持久化存储
    fun saveRefreshToken(token: String) {
        settings.putString(REFRESH_TOKEN_KEY, token)
        Logger.d { "Refresh Token saved to persistent storage" }
    }

    // 从持久化存储中获取 Refresh Token
    fun getRefreshToken(): String? {
        return settings.getStringOrNull(REFRESH_TOKEN_KEY)
    }

    // 清除 Refresh Token
    private fun clearRefreshToken() {
        settings.remove(REFRESH_TOKEN_KEY)
        Logger.d { "Refresh Token cleared from persistent storage" }
    }

    // 实现 TokenProvider 接口的方法
    override suspend fun getAccessToken(): String? {
        val token = accessToken
        if (token != null && !JwtUtils.isTokenExpired(token)) {
            Logger.d { "Access Token found in memory" }
            return token
        }
        Logger.d { "Access Token expired or missing, attempting to refresh" }
        if (refreshToken()) {
            Logger.d { "Access Token refreshed successfully" }
            return accessToken
        }
        throw IllegalStateException("Access Token not found, and refresh token failed, user must login again")
    }

    // 调用 ApiClient 刷新 Token
    private suspend fun refreshToken(): Boolean {
        val refreshToken = getRefreshToken()
            ?: throw IllegalStateException("Refresh Token not found")

        val response = authApiClient.refreshAccessToken(refreshToken)

        if (response.success) {
            val newAccessToken = response.accessToken!!
            val newRefreshToken = response.refreshToken!!
            saveAccessToken(newAccessToken)
            saveRefreshToken(newRefreshToken)
            return true
        }
        return false
    }

    // 清除所有 Token
    fun clearAllTokens() {
        clearAccessToken()
        clearRefreshToken()
        Logger.d { "All tokens cleared" }
    }

    // 检查 Token 是否过期
    fun isTokenExpired(token: String): Boolean {
        return JwtUtils.isTokenExpired(token)
    }
}