package app.penny.core.domain.usecase

import androidx.compose.ui.platform.LocalHapticFeedback
import app.penny.core.data.kvstore.SessionManager
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.LoginResponse
import co.touchlab.kermit.Logger

class LoginUseCase(
    private val apiClient: ApiClient,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(
        email: String, password: String
    ): LoginResponse {

        val response: LoginResponse = apiClient.login(
            email = email,
            password = password
        )


        Logger.d("Retrieved Server Response: $response")

        if (response.success) {
            sessionManager.saveToken(response.token!!)
        }




        return response
    }

}
