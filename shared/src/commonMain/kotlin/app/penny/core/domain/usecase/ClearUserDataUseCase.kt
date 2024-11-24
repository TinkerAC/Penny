package app.penny.core.domain.usecase

import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.UserDataRepository

class ClearUserDataUseCase(
    private val authRepository: AuthRepository,
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke() {
        authRepository.clearToken()
        userDataRepository.clearUserData()
    }
}