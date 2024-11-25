package app.penny.core.domain.usecase

import app.penny.core.network.ApiClient
import co.touchlab.kermit.Logger

class CheckIsEmailRegisteredUseCase(
    private val apiClient: ApiClient
) {
    suspend operator fun invoke(email: String): Boolean {


        val result = apiClient.user.checkIsEmailRegistered(email)

        Logger.d("CheckIsEmailRegisteredUseCase: $email is registered: $result")

        return result.isRegistered
    }
}