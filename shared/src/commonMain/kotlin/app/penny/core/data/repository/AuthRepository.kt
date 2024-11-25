package app.penny.core.data.repository

interface AuthRepository {
    suspend fun login(username: String, password: String): Boolean
    suspend fun refreshToken(): Boolean
    suspend fun logout()


    fun getAccessToken(): String?
    fun isLoggedIn(): Boolean

    // 用于检查 Token 是否过期,本地判断
    fun checkTokenExpired(): Boolean
}