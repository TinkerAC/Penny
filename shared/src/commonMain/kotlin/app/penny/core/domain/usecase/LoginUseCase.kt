package app.penny.core.domain.usecase

import app.penny.core.data.kvstore.SessionManager
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.LoginResponse
import co.touchlab.kermit.Logger

class LoginUseCase(
    private val apiClient: ApiClient,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(username: String, password: String) :LoginResponse{

        val response: LoginResponse = apiClient.login(username, password)

        if (response.success) {
            sessionManager.saveToken(response.token!!)
            Logger.d("Login successful")
        } else {
            Logger.e("Login failed")
        }
        return response
    }

}
