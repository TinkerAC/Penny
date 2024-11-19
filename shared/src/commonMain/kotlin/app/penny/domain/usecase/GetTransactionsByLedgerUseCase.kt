package app.penny.domain.usecase

import app.penny.data.repository.TransactionRepository
import app.penny.domain.model.TransactionModel

class GetTransactionsByLedgerUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(ledgerId: Long): List<TransactionModel> {
        return transactionRepository.getTransactionsByLedger(ledgerId)
    }

}