package app.penny.core.domain.usecase

import app.penny.core.network.ApiClient

class CheckIsEmailRegisteredUseCase(
    private val apiClient: ApiClient
) {
    suspend operator fun invoke(username: String): Boolean {
        return apiClient.checkIsEmailRegistered(username)
    }
}