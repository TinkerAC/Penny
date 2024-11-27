package app.penny.repository

import app.penny.models.Transactions
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class TransactionRepository {


    fun findTransactionByUserIdUpdatedAfter(
        timeStamp: Instant
    ): List<ResultRow> {
        return transaction {
            // 查询所有 updatedAt > timeStamp 的 Transaction 实体
            Transactions.selectAll().where { Transactions.updatedAt greater timeStamp.epochSeconds }
                .toList()
        }
    }

}