package app.penny.services

import app.penny.models.Ledgers
import app.penny.servershared.dto.LedgerDto
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction

class LedgerService {
    fun insertLedgers(
        ledgers: List<LedgerDto>
    ) {
        transaction {
            Ledgers.batchInsert(ledgers) { ledger ->
                this[Ledgers.uuid] = ledger.uuid
                this[Ledgers.name] = ledger.name
                this[Ledgers.coverUri] = ledger.coverUri
                this[Ledgers.createTime] = ledger.createTime
                this[Ledgers.updateTime] = ledger.updateTime
            }
        }
    }
}