// 文件：server/src/main/kotlin/app/penny/services/StatisticsService.kt
package app.penny.services

import app.penny.repository.LedgerRepository
import app.penny.repository.TransactionRepository

class StatisticsService(
    private val ledgerRepository: LedgerRepository,
    private val transactionRepository: TransactionRepository
) {
    fun getUnsyncedDataCount(
        userId: Long,
        lastSyncedAt: Long
    ): UnsyncedDataCount {
        val unsyncedLedgerCount = ledgerRepository.countByUserIdAndUpdatedAfter(
            userId = userId,
            timeStamp = lastSyncedAt
        )
        val unsyncedTransactionCount = transactionRepository.countByUserIdUpdatedAfter(
            userId = userId,
            timeStamp = lastSyncedAt
        )
        return UnsyncedDataCount(
            unsyncedLedgerCount = unsyncedLedgerCount,
            unsyncedTransactionCount = unsyncedTransactionCount
        )
    }
}

data class UnsyncedDataCount(
    val unsyncedLedgerCount: Long,
    val unsyncedTransactionCount: Long
)