// file: shared/src/commonMain/kotlin/app/penny/core/data/repository/impl/AuthRepositoryImpl.kt
package app.penny.core.data.repository.impl

import app.penny.core.data.kvstore.TokenManager
import app.penny.core.data.repository.AuthRepository
import app.penny.core.network.clients.UserApiClient
import app.penny.servershared.dto.responseDto.LoginResponse
import app.penny.servershared.dto.responseDto.RegisterResponse
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

    override suspend fun register(
        email: String,
        password: String,
        uuid: String?
    ): RegisterResponse {
        val response = userApiClient.register(uuid = uuid, email = email, password = password)
        return response

    }

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

    override fun hasLoggedIn(): Boolean {
        val refreshToken = tokenManager.getRefreshToken()
        val hasLoggedIn = refreshToken != null && !tokenManager.isTokenExpired(refreshToken)
        Logger.d { "Has logged in before : $hasLoggedIn" }
        return hasLoggedIn
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


    override suspend fun checkIsEmailRegistered(email: String): Boolean? {
        try {
            val response = userApiClient.checkIsEmailRegistered(email)
            if (response.success) {
                return response.isEmailRegistered
            } else {
                Logger.e { "Check email registered failed: ${response.message}" }
                return null
            }
        } catch (e: Exception) {
            Logger.e { "Check email registered failed: ${e.message}" }
            return null
        }


    }
}