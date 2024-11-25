// file: server/src/main/kotlin/app/penny/services/LedgerService.kt
package app.penny.services

import app.penny.models.Ledgers
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

class LedgerService {
    fun insertLedgers(
        ledgers: List<LedgerDto>,
        userId: Int
    ) {
        transaction {
            Ledgers.batchInsert(ledgers) { ledger ->
                this[Ledgers.userId] = userId
                this[Ledgers.uuid] = ledger.uuid
                this[Ledgers.name] = ledger.name
                this[Ledgers.currencyCode] = ledger.currencyCode
                this[Ledgers.createdAt] = ledger.createdAt
                this[Ledgers.updatedAt] = ledger.updatedAt
            }
        }
    }

    fun getLedgersByUserIdAfterLastSync(
        userId: Int,
        lastSyncedAt: Long
    ): List<LedgerDto> {
        return transaction {
            Ledgers.selectAll()
                .where { (Ledgers.userId eq userId) and (Ledgers.updatedAt greater lastSyncedAt) }.map {
                LedgerDto(
                    uuid = it[Ledgers.uuid],
                    name = it[Ledgers.name],
                    currencyCode = it[Ledgers.currencyCode],
                    createdAt = it[Ledgers.createdAt],
                    updatedAt = it[Ledgers.updatedAt]
                )
            }
        }
    }
}
