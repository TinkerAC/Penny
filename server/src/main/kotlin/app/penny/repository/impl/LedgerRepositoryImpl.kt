// 文件：server/src/main/kotlin/app/penny/repository/LedgerRepositoryImpl.kt
package app.penny.repository.impl

import app.penny.models.Ledgers
import app.penny.models.toLedgerDto
import app.penny.repository.LedgerRepository
import app.penny.servershared.dto.LedgerDto
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class LedgerRepositoryImpl : LedgerRepository {

    override fun findUpdatedAfter(lastUpdatedAt: Long): List<LedgerDto> {
        return transaction {
            Ledgers
                .selectAll().where { Ledgers.updatedAt greater lastUpdatedAt }
                .map { it.toLedgerDto() }
        }
    }

    override fun insert(ledgers: List<LedgerDto>) {
        transaction {
            Ledgers.batchInsert(ledgers) { ledger ->
                this[Ledgers.uuid] = ledger.uuid
                this[Ledgers.name] = ledger.name
                this[Ledgers.currencyCode] = ledger.currencyCode
                this[Ledgers.createdAt] = ledger.createdAt
                this[Ledgers.updatedAt] = ledger.updatedAt
                this[Ledgers.userId] = ledger.userId
            }
        }
    }

    override fun findByUserIdAndUpdatedAtAfter(userId: Long, lastSyncedAt: Long): List<LedgerDto> {
        return transaction {
            Ledgers
                .selectAll()
                .where { (Ledgers.updatedAt greater lastSyncedAt) and (Ledgers.userId eq userId) }
                .map { it.toLedgerDto() }
        }
    }

    override fun countByUserIdAndUpdatedAfter(userId: Long, timeStamp: Long): Long {
        return transaction {
            Ledgers
                .selectAll()
                .where { (Ledgers.userId eq userId) and (Ledgers.updatedAt greater timeStamp) }
                .count()
        }
    }

    override fun upsertByUuid(ledger: LedgerDto) {
        transaction {
            // 尝试插入，如果冲突则忽略
            val insertedCount = Ledgers.insertIgnore { row ->
                row[uuid] = ledger.uuid  //check whether the ledger exists by uuid
                row[name] = ledger.name
                row[currencyCode] = ledger.currencyCode
                row[createdAt] = ledger.createdAt
                row[updatedAt] = ledger.updatedAt
                row[userId] = ledger.userId
            }.insertedCount

            if (insertedCount == 0) {
                Ledgers.update({ Ledgers.uuid eq ledger.uuid }) { row ->
                    row[name] = ledger.name
                    row[currencyCode] = ledger.currencyCode
                    row[createdAt] = ledger.createdAt
                    row[updatedAt] = ledger.updatedAt
                    row[userId] = ledger.userId
                }
            }
        }


    }
}