package app.penny.core.domain.usecase

import app.penny.core.data.repository.LedgerRepository

class DeleteLedgerUseCase(
    private val ledgerRepository: LedgerRepository
) {


    suspend operator fun invoke(ledgerId: Long) = ledgerRepository.deleteLedger(ledgerId)

}