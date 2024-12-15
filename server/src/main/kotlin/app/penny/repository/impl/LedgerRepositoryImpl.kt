// 文件：server/src/main/kotlin/app/penny/repository/LedgerRepositoryImpl.kt
package app.penny.repository.impl

import app.penny.models.Ledgers
import app.penny.models.Users
import app.penny.repository.LedgerRepository
import app.penny.servershared.dto.LedgerDto
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class LedgerRepositoryImpl : LedgerRepository {

    override fun insert(userId: Long, ledgers: List<LedgerDto>) {
        transaction {
            Ledgers.batchInsert(ledgers) { ledger ->
                this[Ledgers.uuid] = ledger.uuid
                this[Ledgers.name] = ledger.name
                this[Ledgers.currencyCode] = ledger.currencyCode
                this[Ledgers.createdAt] = ledger.createdAt
                this[Ledgers.updatedAt] = ledger.updatedAt
                this[Ledgers.userId] = userId
            }
        }
    }

    override fun findByUserIdAndUpdatedAtAfter(userId: Long, lastSyncedAt: Long): List<LedgerDto> {
        return transaction {
            // 使用 Exposed 的 DSL 构建查询
            (Ledgers innerJoin Users)
                .selectAll().where {
                    (Ledgers.userId eq userId) and (Ledgers.updatedAt greater lastSyncedAt)
                }
                .map { row ->
                    LedgerDto(
                        userUuid = row[Users.uuid],
                        uuid = row[Ledgers.uuid],
                        name = row[Ledgers.name],
                        currencyCode = row[Ledgers.currencyCode],
                        createdAt = row[Ledgers.createdAt],
                        updatedAt = row[Ledgers.updatedAt]
                    )
                }
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
            }.insertedCount

            if (insertedCount == 0) {
                Ledgers.update({ Ledgers.uuid eq ledger.uuid }) { row ->
                    row[name] = ledger.name
                    row[currencyCode] = ledger.currencyCode
                    row[createdAt] = ledger.createdAt
                    row[updatedAt] = ledger.updatedAt
                }
            }
        }


    }
}