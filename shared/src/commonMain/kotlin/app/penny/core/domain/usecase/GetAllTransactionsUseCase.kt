package app.penny.core.domain.usecase

import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.model.TransactionModel
import co.touchlab.kermit.Logger

class GetAllTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(): List<TransactionModel> {
        val result = transactionRepository.findAllTransactions()
        Logger.d("Get all transactions: ${result.size}")
        return result
    }
}