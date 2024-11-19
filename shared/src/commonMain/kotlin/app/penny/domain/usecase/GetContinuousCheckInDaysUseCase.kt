package app.penny.domain.usecase

import app.penny.data.repository.UserDataRepository


class GetContinuousCheckInDaysUseCase(
    private val userDataRepository: app.penny.data.repository.UserDataRepository
) {

    suspend operator fun invoke(): Int {
        val days = userDataRepository.getContinuousCheckInDays()
        return days
    }


}