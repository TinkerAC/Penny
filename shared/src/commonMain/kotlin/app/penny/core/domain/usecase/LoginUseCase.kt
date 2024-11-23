package app.penny.core.domain.usecase

import app.penny.core.data.kvstore.SessionManager
import app.penny.core.network.ApiClient
import app.penny.core.network.dto.LoginResponseDto

class LoginUseCase(
    private val apiClient: ApiClient,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(username: String, password: String) {

        val responseDto: LoginResponseDto = apiClient.login(username, password)
        sessionManager.saveToken(responseDto.token)

    }

}
