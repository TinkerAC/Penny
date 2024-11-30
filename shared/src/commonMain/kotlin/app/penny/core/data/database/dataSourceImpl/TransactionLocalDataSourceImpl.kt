package app.penny.core.data.database.dataSourceImpl

import app.penny.core.data.database.TransactionLocalDataSource
import app.penny.database.TransactionEntity
import app.penny.database.TransactionsQueries

class TransactionLocalDataSourceImpl(
    private val transactionsQueries: TransactionsQueries
) : TransactionLocalDataSource {


    override fun insert(transaction: TransactionEntity) {
        transactionsQueries.insert(
            uuid = transaction.uuid,
            ledger_uuid = transaction.ledger_uuid,
            transaction_date = transaction.transaction_date,
            category_name = transaction.category_name,
            transaction_type = transaction.transaction_type,
            amount = transaction.amount,
            currency_code = transaction.currency_code,
            remark = transaction.remark,
            screenshot_uri = transaction.screenshot_uri
        )
    }

    override fun upsertByUuid(transaction: TransactionEntity) {
        transactionsQueries.upsertByUuid(
            uuid = transaction.uuid,
            ledger_uuid = transaction.ledger_uuid,
            transaction_date = transaction.transaction_date,
            category_name = transaction.category_name,
            transaction_type = transaction.transaction_type,
            amount = transaction.amount,
            currency_code = transaction.currency_code,
            remark = transaction.remark,
            screenshot_uri = transaction.screenshot_uri
        )
    }

    override fun findByUuid(uuid: String): TransactionEntity? {
        return transactionsQueries.findByUuid(uuid).executeAsOneOrNull()
    }

    override fun findAll(): List<TransactionEntity> {
        return transactionsQueries.findAll().executeAsList()
    }

    override fun findByLedgerUuid(ledgerUuid: String): List<TransactionEntity> {
        return transactionsQueries.findByLedgerUuid(
            ledger_uuid = ledgerUuid
        ).executeAsList()
    }

    override fun findByLedgerUuidAndTransactionDateBetween(
        ledgerUuid: String,
        startTimestamp: Long,
        endTimestamp: Long
    ): List<TransactionEntity> {
        return transactionsQueries.findByLedgerUuidAndTransactionDateBetween(
            ledger_uuid = ledgerUuid,
            transaction_date_ = startTimestamp,
            transaction_date = endTimestamp
        ).executeAsList()
    }

    override fun updateByUuid(transaction: TransactionEntity) {
        transactionsQueries.update(
            ledger_uuid = transaction.ledger_uuid,
            transaction_date = transaction.transaction_date,
            category_name = transaction.category_name,
            transaction_type = transaction.transaction_type,
            amount = transaction.amount,
            currency_code = transaction.currency_code,
            remark = transaction.remark,
            screenshot_uri = transaction.screenshot_uri,
            uuid = transaction.uuid
        )
    }

    override fun deleteByUuid(uuid: String) {
        transactionsQueries.deleteByUuid(uuid)
    }

    override fun count(): Long {
        return transactionsQueries.count().executeAsOne()
    }

    override fun countByUserUuidAndUpdatedAtAfter(userUuid: String, timestamp: Long): Long {
        return transactionsQueries.countByUserUuidAndUpdatedAtAfter(
            user_uuid = userUuid,
            updated_at = timestamp
        ).executeAsOne()
    }

    override fun findByUserUuid(userUuid: String): List<TransactionEntity> {
        return transactionsQueries.findByUserUuid(user_uuid = userUuid).executeAsList()
    }

    override fun findByUserUuidAndUpdatedAtAfter(
        userUuid: String,
        timestamp: Long
    ): List<TransactionEntity> {
        return transactionsQueries.findByUserUuidAndUpdatedAtAfter(
            user_uuid = userUuid,
            updated_at = timestamp
        ).executeAsList()
    }

    override fun countByLedgerUuid(ledgerUuid: String): Long {
        return transactionsQueries.countByLedgerUuid(ledger_uuid = ledgerUuid).executeAsOne()
    }
}