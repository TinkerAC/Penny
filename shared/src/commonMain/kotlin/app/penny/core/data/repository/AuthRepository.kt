package app.penny.core.data.repository

import app.penny.servershared.dto.responseDto.LoginResponse

interface AuthRepository {
    suspend fun login(username: String, password: String): LoginResponse

    suspend fun getAccessToken(): String?
    suspend fun logout()

    suspend fun checkIsEmailRegistered(email: String): Boolean?

    fun isLoggedIn(): Boolean


    fun clearToken()


    fun saveAccessToken(accessToken: String)
    fun saveRefreshToken(refreshToken: String)



}