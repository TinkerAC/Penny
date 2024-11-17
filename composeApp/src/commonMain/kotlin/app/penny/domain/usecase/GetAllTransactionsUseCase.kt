package app.penny.domain.usecase

import app.penny.data.repository.TransactionRepository
import app.penny.domain.model.TransactionModel
import co.touchlab.kermit.Logger

class GetAllTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(): List<TransactionModel> {
        val result = transactionRepository.getAllTransactions()
        Logger.d("Get all transactions: ${result.size}")
        return result
    }
}