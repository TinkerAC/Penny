package app.penny.domain.usecase

import app.penny.data.repository.TransactionRepository

class GetAllTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke() = transactionRepository.getAllTransactions()
}