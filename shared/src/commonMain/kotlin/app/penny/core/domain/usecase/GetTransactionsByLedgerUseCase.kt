package app.penny.core.domain.usecase

import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.model.TransactionModel

class GetTransactionsByLedgerUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(ledgerId: Long): List<TransactionModel> {
        return transactionRepository.findTransactionsByLedger(ledgerId)
    }

}