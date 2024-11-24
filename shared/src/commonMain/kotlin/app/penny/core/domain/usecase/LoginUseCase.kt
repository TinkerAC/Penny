package app.penny.core.domain.usecase

import androidx.compose.ui.platform.LocalHapticFeedback
import app.penny.core.data.kvstore.SessionManager
import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.LoginResponse
import app.penny.servershared.dto.UserDto
import co.touchlab.kermit.Logger

class LoginUseCase(
    private val apiClient: ApiClient,
    private val authRepository: AuthRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(
        email: String, password: String
    ): LoginResponse {

        val response: LoginResponse = apiClient.login(
            email = email,
            password = password
        )


        if (response.success) {
            val userDto: UserDto = response.userDto!!
            authRepository.saveToken(response.token!!)
            if (userDto.username != null) {
                userDataRepository.setUserName(
                    userDto.username
                )
            }
            userDataRepository.setUserEmail(
                userDto.email,
            )

        }

        return response
    }

}
