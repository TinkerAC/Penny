package app.penny.core.data.repository

import app.penny.servershared.dto.responseDto.LoginResponse
import app.penny.servershared.dto.responseDto.RegisterResponse

interface AuthRepository {
    suspend fun login(username: String, password: String): LoginResponse
    suspend fun register(email: String, password: String, uuid: String?): RegisterResponse
    suspend fun getAccessToken(): String?
    suspend fun logout()

    suspend fun checkIsEmailRegistered(email: String): Boolean?


    // detect if the user is logged in (check accessToken)
    fun isLoggedIn(): Boolean


    // detect if the user has logged in before ( has refresh token persisted)
    fun hasLoggedIn(): Boolean


    fun clearToken()


    fun saveAccessToken(accessToken: String)
    fun saveRefreshToken(refreshToken: String)


}