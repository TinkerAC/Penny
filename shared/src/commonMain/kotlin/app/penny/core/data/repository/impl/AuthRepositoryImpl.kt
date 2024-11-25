package app.penny.core.data.repository.impl

import app.penny.core.data.kvstore.TokenStorage
import app.penny.core.data.repository.AuthRepository
import app.penny.core.network.ApiClient

class AuthRepositoryImpl(
    private val tokenStorage: TokenStorage,
    private val apiClient: ApiClient
) : AuthRepository {

    override suspend fun login(username: String, password: String): Boolean {
        val response = apiClient.login(username, password)
        if (response.success) {
            val accessToken = response.accessToken!!
            val refreshToken = response.refreshToken!!

            tokenStorage.saveAccessToken(accessToken) // 存储 Access Token（内存）
            tokenStorage.saveRefreshToken(refreshToken) // 存储 Refresh Token（持久化）
            return true
        }
        return false
    }

    override suspend fun refreshToken(): Boolean {
        val refreshToken = tokenStorage.getRefreshToken()
            ?: throw IllegalStateException("Refresh Token not found")

        val response = apiClient.refreshAccessToken(refreshToken)
        if (response.isSuccessful) {
            val newAccessToken = response.accessToken
            tokenStorage.saveAccessToken(newAccessToken) // 更新 Access Token
            return true
        }
        return false
    }

    override fun getAccessToken(): String? {
        return tokenStorage.getAccessToken()
    }

    override suspend fun logout() {
        tokenStorage.clearAccessToken()
        tokenStorage.clearRefreshToken()
    }

    override fun isLoggedIn(): Boolean {
        return tokenStorage.getAccessToken() != null
    }
}