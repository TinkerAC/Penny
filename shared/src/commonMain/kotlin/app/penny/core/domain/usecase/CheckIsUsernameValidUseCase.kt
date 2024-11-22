package app.penny.core.domain.usecase

import app.penny.core.network.ApiClient

class CheckIsUsernameValidUseCase(
    private val apiClient: ApiClient
) {
    suspend operator fun invoke(username: String): Boolean {
        return apiClient.checkIsUsernameValid(username)
    }
}