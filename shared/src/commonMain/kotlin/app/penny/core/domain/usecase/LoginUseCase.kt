package app.penny.core.domain.usecase

import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.LoginResponse

class LoginUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String, password: String
    ): LoginResponse {
        val response = authRepository.login(email, password)

        if (response.success) {
            val accessToken = response.accessToken!!
            val refreshToken = response.refreshToken!!

            authRepository.saveAccessToken(accessToken)
            authRepository.saveRefreshToken(refreshToken)
        }

        return response
    }

}
