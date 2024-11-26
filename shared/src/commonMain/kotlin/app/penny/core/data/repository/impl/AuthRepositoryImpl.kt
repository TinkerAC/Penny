// file: shared/src/commonMain/kotlin/app/penny/core/data/repository/impl/AuthRepositoryImpl.kt
package app.penny.core.data.repository.impl

import app.penny.core.data.kvstore.TokenManager
import app.penny.core.data.repository.AuthRepository
import app.penny.core.network.ApiClient
import app.penny.core.network.clients.AuthApiClient
import app.penny.core.network.clients.UserApiClient
import app.penny.servershared.dto.LoginResponse
import co.touchlab.kermit.Logger

class AuthRepositoryImpl(
    private val userApiClient: UserApiClient,
    private val tokenManager: TokenManager
) : AuthRepository {
    override suspend fun login(username: String, password: String): LoginResponse {
        val response = userApiClient.login(username, password)
        if (response.success) {
            val accessToken = response.accessToken!!
            val refreshToken = response.refreshToken!!
            tokenManager.saveAccessToken(accessToken)
            tokenManager.saveRefreshToken(refreshToken)
            Logger.d { "User logged in successfully" }
        } else {
            Logger.e { "Login failed: ${response.message}" }
        }
        return response
    }


    // 始终返回有效的 Access Token，否则抛出异常

    override suspend fun getAccessToken(): String? {
        return tokenManager.getAccessToken()
    }

    override suspend fun logout() {
        tokenManager.clearAllTokens()
        Logger.d { "User logged out successfully" }
    }

    override fun isLoggedIn(): Boolean {
        val accessToken = tokenManager.getAccessTokenFromMemory()
        val isValid = accessToken != null && !tokenManager.isTokenExpired(accessToken)
        Logger.d { "Is logged in: $isValid" }
        return isValid
    }


    override fun clearToken() {
        tokenManager.clearAllTokens()
    }


    override fun saveAccessToken(accessToken: String) {
        tokenManager.saveAccessToken(accessToken)
    }

    override fun saveRefreshToken(refreshToken: String) {
        tokenManager.saveRefreshToken(refreshToken)
    }
}