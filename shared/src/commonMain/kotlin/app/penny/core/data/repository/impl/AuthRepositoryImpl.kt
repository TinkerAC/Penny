package app.penny.core.data.repository.impl

import app.penny.core.data.kvstore.SessionManager
import app.penny.core.data.repository.AuthRepository

class AuthRepositoryImpl(
    private val sessionManager: SessionManager
) : AuthRepository {
    override fun isLoggedIn(): Boolean {
        return sessionManager.getToken() != null
    }

    override fun getToken(): String? {
        return sessionManager.getToken()
    }

    override fun saveToken(token: String) {
        sessionManager.saveToken(token)
    }

    override fun clearToken() {
        sessionManager.clearToken()
    }
}