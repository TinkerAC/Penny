package app.penny.repository

import app.penny.models.Ledger
import app.penny.models.Ledgers
import app.penny.models.User
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.selectAll

class LedgerRepository {

    // 查询所有 updatedAt > lastUpdatedAt 的 Ledger 实体
    fun getUpdatedLedger(lastUpdatedAt: Long): List<Ledger> {
        return transaction {
            // 使用 DAO 的 find 方法
            Ledger.find { Ledgers.updatedAt greater lastUpdatedAt }.toList()
        }
    }

    fun insertLedgers(ledgers: List<LedgerDTO>) {
        transaction {
            ledgers.forEach {
                Ledger.new {
                    name = it.name
                    currencyCode = it.currencyCode
                    iconUri = it.iconUri
                    createdAt = it.createdAt
                    updatedAt = it.updatedAt
                }
            }

        }
    }
}