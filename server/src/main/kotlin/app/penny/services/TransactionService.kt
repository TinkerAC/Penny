// 文件：server/src/main/kotlin/app/penny/services/TransactionService.kt
package app.penny.services

import app.penny.repository.TransactionRepository
import app.penny.servershared.dto.TransactionDto

class TransactionService(
    private val transactionRepository: TransactionRepository
) {
    fun findUnsyncedTransactions(
        userId: Long,
        lastSyncedAt: Long
    ): List<TransactionDto> {
        return transactionRepository.findByUserIdAndUpdatedAfter(
            userId = userId,
            timeStamp = lastSyncedAt
        )
    }

    fun insertTransactions(
        transactions: List<TransactionDto>,
        userId: Long
    ) {
        val transactionsWithUserId = transactions.map { transaction ->
            transaction.copy(userId = userId)
        }
        transactionRepository.insert(transactionsWithUserId)
    }



    fun upsertTransactionByUuid(
        transaction: TransactionDto,
    ) {
        transactionRepository.upsertByUuid(
            transaction
        )
    }
}