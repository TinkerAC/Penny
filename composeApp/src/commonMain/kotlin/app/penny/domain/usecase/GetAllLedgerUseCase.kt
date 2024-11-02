package app.penny.domain.usecase

import app.penny.data.repository.LedgerRepository

class GetAllLedgerUseCase(
    private val ledgerRepository: LedgerRepository

) {

    suspend operator fun invoke() = ledgerRepository.getAllLedgers()

}