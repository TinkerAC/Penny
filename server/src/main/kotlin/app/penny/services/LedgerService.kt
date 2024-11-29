// 文件：server/src/main/kotlin/app/penny/services/LedgerService.kt
package app.penny.services

import app.penny.repository.LedgerRepository
import app.penny.servershared.dto.LedgerDto

class LedgerService(
    private val ledgerRepository: LedgerRepository
) {
    fun insertLedgers(
        ledgers: List<LedgerDto>,
        userId: Long
    ) {
        val ledgersWithUserId = ledgers.map { ledger ->
            ledger.copy(userId = userId)
        }
        ledgerRepository.insert(ledgersWithUserId)
    }

    fun getLedgersByUserIdAfterLastSync(
        userId: Long,
        lastSyncedAt: Long
    ): List<LedgerDto> {
        return ledgerRepository.findByUserIdAndUpdatedAtAfter(userId, lastSyncedAt)
    }
}