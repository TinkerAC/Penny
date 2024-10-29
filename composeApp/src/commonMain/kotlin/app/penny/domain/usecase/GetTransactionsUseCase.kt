package app.penny.domain.usecase

import app.penny.data.repository.TransactionRepository
import app.penny.domain.model.TransactionModel

class GetTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(): List<TransactionModel> {
        return transactionRepository.getAllTransactions()
    }

}