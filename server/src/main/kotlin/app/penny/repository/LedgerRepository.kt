// 文件：server/src/main/kotlin/app/penny/repository/LedgerRepository.kt
package app.penny.repository

import app.penny.servershared.dto.LedgerDto

interface LedgerRepository {
    fun insert(userId: Long, ledgers: List<LedgerDto>)
    fun findByUserIdAndUpdatedAtAfter(userId: Long, lastSyncedAt: Long): List<LedgerDto>
    fun countByUserIdAndUpdatedAfter(userId: Long, timeStamp: Long): Long
    fun upsertByUuid(ledger: LedgerDto, userId: Long)
}