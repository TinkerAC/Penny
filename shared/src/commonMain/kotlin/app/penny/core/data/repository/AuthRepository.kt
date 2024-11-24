package app.penny.core.data.repository

interface AuthRepository {
    fun isLoggedIn(): Boolean
    fun getToken(): String?
    fun saveToken(token: String)
    fun clearToken()

//    fun getRefreshToken(): String?
//    fun saveRefreshToken(token: String)
//    fun clearRefreshToken()

    fun refreshToken(): String




}