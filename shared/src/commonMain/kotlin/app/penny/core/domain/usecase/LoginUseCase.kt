package app.penny.core.domain.usecase

import app.penny.core.network.ApiClient

class LoginUseCase(
    private val apiClient: ApiClient,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(username: String, password: String): String {

        val response = apiClient.login(username, password)
        throw NotImplementedError("Not implemented yet")
//        if (response.isSuccessful) {
//            return response.body() ?: ""
//        } else {
//            throw Exception("Login failed")
//        }


    }
}
