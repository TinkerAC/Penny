package app.penny.core.data.repository

interface AuthRepository {
    suspend fun login(username: String, password: String): Boolean
    suspend fun refreshToken(): Boolean
    suspend fun logout()


    fun getAccessToken(): String?
    fun isLoggedIn(): Boolean
}