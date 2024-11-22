package app.penny.core.domain.usecase

import app.penny.core.data.repository.UserDataRepository


class GetContinuousCheckInDaysUseCase(
    private val userDataRepository: UserDataRepository
) {

    suspend operator fun invoke(): Int {
        val days = userDataRepository.getContinuousCheckInDays()
        return days
    }


}