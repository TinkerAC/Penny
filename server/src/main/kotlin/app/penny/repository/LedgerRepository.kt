package app.penny.repository

import app.penny.models.Ledgers
import app.penny.models.Users
import app.penny.models.toLedgerDto
import app.penny.servershared.dto.LedgerDto
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.boot.autoconfigure.security.SecurityProperties.User

class LedgerRepository {

    // 查询所有 updatedAt > lastUpdatedAt 的 Ledger 实体
    fun getUpdatedLedger(lastUpdatedAt: Long): List<LedgerDto> {
        return Ledgers
            .selectAll()
            .where { Ledgers.updatedAt greater lastUpdatedAt }
            .toList()
            .map { it.toLedgerDto() }
    }


    fun insertLedgers(ledgers: List<LedgerDto>) {
        transaction {
            ledgers.forEach {
                //use DSL to insert
                Ledgers.insert { ledger ->
                    ledger[uuid] = it.uuid
                    ledger[name] = it.name
                    ledger[currencyCode] = it.currencyCode
                    ledger[createdAt] = it.createdAt
                    ledger[updatedAt] = it.updatedAt
                }

            }

        }
    }

    fun findByUserIdAndUpdatedAtAfter(userId: Long, lastSyncedAt: Long): List<LedgerDto> {
        return transaction {
            Ledgers.selectAll().where {
                (Ledgers.updatedAt greater lastSyncedAt) and
                        (Ledgers.userId.eq(userId))
            }.map {
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