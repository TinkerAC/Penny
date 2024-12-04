package app.penny.core.domain.usecase

import app.penny.core.data.repository.AuthRepository
import app.penny.servershared.dto.responseDto.LoginResponse
import co.touchlab.kermit.Logger

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
            Logger.d(
                response.toString()
            )
            authRepository.saveAccessToken(accessToken)
            authRepository.saveRefreshToken(refreshToken)
        }

        return response
    }

}
