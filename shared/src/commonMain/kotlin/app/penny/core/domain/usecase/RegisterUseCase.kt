package app.penny.core.domain.usecase

import app.penny.core.network.ApiClient
import app.penny.servershared.dto.responseDto.RegisterResponse

class RegisterUseCase(
    private val apiClient: ApiClient
) {

    suspend operator fun invoke(email: String, password: String, uuid: String?): RegisterResponse {
        //register user,return the message from the server
        return apiClient.user.register(
            uuid = uuid,
            email = email,
            password = password
        )


    }
}