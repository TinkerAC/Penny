package app.penny.core.domain.usecase

import app.penny.core.data.repository.TransactionRepository

class GetTransactionsCountUseCase(
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(): Long {
        val count = transactionRepository.count()
        return count
    }
}