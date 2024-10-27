package app.penny.domain.usecase

import app.penny.data.repository.TransactionRepository
import app.penny.domain.model.Transaction

class GetTransactionsUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(): List<Transaction> {
        return repository.getTransactions()
    }
}
