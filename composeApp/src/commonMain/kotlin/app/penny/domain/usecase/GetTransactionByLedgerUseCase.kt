package app.penny.domain.usecase

import app.penny.data.repository.TransactionRepository

class GetTransactionByLedgerUseCase (
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(ledgerId: Long) = transactionRepository.getTransactionsByLedger(ledgerId)

}