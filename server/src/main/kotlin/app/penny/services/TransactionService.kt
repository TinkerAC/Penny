package app.penny.services

import app.penny.repository.TransactionRepository
import app.penny.servershared.dto.TransactionDto
import kotlinx.datetime.Instant
import app.penny.models.toTransactionDto

class TransactionService(
    val transactionRepository: TransactionRepository
) {
    fun findUnsyncedTransactions(
        userId: Long,
        lastSyncedAt: Long

        ): List<TransactionDto> {
        return transactionRepository.findTransactionByUserIdUpdatedAfter(
            userId = userId,
            timeStamp = lastSyncedAt
        ).map {
            it.toTransactionDto()
        }

    }
}