// file: server/src/main/kotlin/app/penny/services/LedgerService.kt
package app.penny.services

import app.penny.models.Ledgers
import app.penny.repository.LedgerRepository
import app.penny.servershared.dto.LedgerDto
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class LedgerService (
    private val ledgerRepository: LedgerRepository
){
    fun insertLedgers(
        ledgers: List<LedgerDto>,
        userId: Int
    ) {
        transaction {
            Ledgers.batchInsert(ledgers) { ledger ->
                this[Ledgers.uuid] = ledger.uuid
                this[Ledgers.name] = ledger.name
                this[Ledgers.currencyCode] = ledger.currencyCode
                this[Ledgers.createdAt] = ledger.createdAt
                this[Ledgers.updatedAt] = ledger.updatedAt
            }
        }
    }

    fun getLedgersByUserIdAfterLastSync(
        userId: Long,
        lastSyncedAt: Long
    ): List<LedgerDto> {
       return ledgerRepository.findByUserIdAndUpdatedAtAfter(userId, lastSyncedAt)
    }
}
