package app.penny.domain.usecase

import app.penny.data.repository.LedgerRepository

class DeleteLedgerUseCase(
    private val ledgerRepository: LedgerRepository
) {


    suspend operator fun invoke(ledgerId: Long) = ledgerRepository.deleteLedger(ledgerId)

}