package app.penny.services

import app.penny.repository.TransactionRepository
import app.penny.servershared.dto.TransactionDto
import kotlinx.datetime.Instant
import app.penny.models.Transaction

class TransactionService(
    val transactionRepository: TransactionRepository
) {
    fun findUnsyncedTransactions(lastSyncedAt: Instant): List<TransactionDto> {
        return transactionRepository.findTransactionByUserIdUpdatedAfter(lastSyncedAt)
            .map {
                TransactionDto(
                    uuid = it[app.penny.models.Transactions.uuid],
                    ledgerUuid = it[app.penny.models.Transactions.ledgerUuid],
                    amount = it[app.penny.models.Transactions.amount],
                    currencyCode = it[app.penny.models.Transactions.currencyCode],
                    createdAt = it[app.penny.models.Transactions.createdAt],
                    updatedAt = it[app.penny.models.Transactions.updatedAt]
                )
            }

    }
}