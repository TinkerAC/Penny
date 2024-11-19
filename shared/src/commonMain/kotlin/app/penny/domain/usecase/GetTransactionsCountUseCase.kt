package app.penny.domain.usecase

import app.penny.data.repository.TransactionRepository

class GetTransactionsCountUseCase(
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(): Int {
        val count = transactionRepository.getTransactionsCount()
        return count
    }
}