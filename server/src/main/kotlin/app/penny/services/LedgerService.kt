// 文件：server/src/main/kotlin/app/penny/services/LedgerService.kt
package app.penny.services

import app.penny.repository.LedgerRepository
import app.penny.servershared.dto.entityDto.LedgerDto

class LedgerService(
    private val ledgerRepository: LedgerRepository
) {

    fun getLedgersByUserIdAfterLastSync(
        userId: Long,
        lastSyncedAt: Long
    ): List<LedgerDto> {
        return ledgerRepository.findByUserIdAndUpdatedAtAfter(userId, lastSyncedAt)
    }




    fun upsertLedgerByUuid(
        ledger: LedgerDto,
    ) {
        ledgerRepository.upsertByUuid(
            ledger
        )
    }
}