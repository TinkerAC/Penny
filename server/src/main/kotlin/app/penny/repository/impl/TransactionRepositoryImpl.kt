// 文件：server/src/main/kotlin/app/penny/repository/TransactionRepositoryImpl.kt
package app.penny.repository.impl

import app.penny.models.Transactions
import app.penny.models.toTransactionDto
import app.penny.repository.TransactionRepository
import app.penny.servershared.dto.TransactionDto
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class TransactionRepositoryImpl : TransactionRepository {

    override fun findByUserIdAndUpdatedAfter(
        userId: Long,
        timeStamp: Long
    ): List<TransactionDto> {
        return transaction {
            Transactions
                .selectAll()
                .where { (Transactions.userId eq userId) and (Transactions.updatedAt greater timeStamp) }
                .map { it.toTransactionDto() }
        }
    }

    override fun findByUuid(uuid: String): TransactionDto? {
        return transaction {
            Transactions
                .selectAll()
                .where { Transactions.uuid eq uuid }
                .map { it.toTransactionDto() }
                .singleOrNull()
        }
    }

    override fun insert(transactions: List<TransactionDto>) {
        transaction {
            Transactions.batchInsert(transactions) { transactionDto ->
                this[Transactions.uuid] = transactionDto.uuid
                this[Transactions.ledgerUuid] = transactionDto.ledgerUuid
                this[Transactions.amount] = transactionDto.amount
                this[Transactions.createdAt] = transactionDto.createdAt
                this[Transactions.updatedAt] = transactionDto.updatedAt
                this[Transactions.remark] = transactionDto.remark
                this[Transactions.transactionType] = transactionDto.transactionType
                this[Transactions.transactionDate] = transactionDto.transactionDate
                this[Transactions.categoryName] = transactionDto.categoryName
                this[Transactions.currencyCode] = transactionDto.currencyCode
                this[Transactions.userId] = transactionDto.userId
            }
        }
    }

    override fun countByUserIdUpdatedAfter(userId: Long, timeStamp: Long): Long {
        return transaction {
            Transactions
                .selectAll()
                .where { (Transactions.userId eq userId) and (Transactions.updatedAt greater timeStamp) }
                .count()
        }
    }


    override fun upsert(transaction: TransactionDto) {
        transaction {
            val insertedCount = Transactions.insertIgnore { row ->
                row[uuid] = transaction.uuid
                row[ledgerUuid] = transaction.ledgerUuid
                row[amount] = transaction.amount
                row[createdAt] = transaction.createdAt
                row[updatedAt] = transaction.updatedAt
                row[remark] = transaction.remark
                row[transactionType] = transaction.transactionType
                row[transactionDate] = transaction.transactionDate
                row[categoryName] = transaction.categoryName
                row[currencyCode] = transaction.currencyCode
                row[userId] = transaction.userId
            }.insertedCount

            if (insertedCount == 0) {
                Transactions.update({ Transactions.uuid eq transaction.uuid }) { row ->
                    row[ledgerUuid] = transaction.ledgerUuid
                    row[amount] = transaction.amount
                    row[createdAt] = transaction.createdAt
                    row[updatedAt] = transaction.updatedAt
                    row[remark] = transaction.remark
                    row[transactionType] = transaction.transactionType
                    row[transactionDate] = transaction.transactionDate
                    row[categoryName] = transaction.categoryName
                    row[currencyCode] = transaction.currencyCode
                    row[userId] = transaction.userId
                }
            }
        }
    }
}