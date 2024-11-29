package app.penny.core.domain.usecase

import app.penny.core.data.repository.LedgerRepository

class GetAllLedgerUseCase(
    private val ledgerRepository: LedgerRepository

) {

    suspend operator fun invoke() = ledgerRepository.findAll()

}