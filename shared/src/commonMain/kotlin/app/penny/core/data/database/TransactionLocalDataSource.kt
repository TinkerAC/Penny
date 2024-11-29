package app.penny.core.data.database

import app.penny.database.TransactionEntity
import app.penny.database.TransactionsQueries


class TransactionLocalDataSource(
    private val transactionsQueries: TransactionsQueries
) {

    fun insertTransaction(transaction: TransactionEntity) {
        transactionsQueries.insertTransaction(
            uuid = transaction.uuid,
            ledger_uuid = transaction.ledger_uuid,
            transaction_date = transaction.transaction_date,
            category_name = transaction.category_name,
            transaction_type = transaction.transaction_type,
            amount = transaction.amount,
            currency_code = transaction.currency_code,
            remark = transaction.remark,
            screenshot_uri = transaction.screenshot_uri,
        )
    }

    fun getAllTransactions(): List<TransactionEntity> {
        return transactionsQueries.getAllTransactions().executeAsList()
    }

    fun updateTransaction(transaction: TransactionEntity) {
        transactionsQueries.updateTransaction(
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


    fun deleteTransaction(uuid: String) {
        transactionsQueries.deleteTransaction(
            uuid = uuid
        )
    }

    fun getTransactionsByLedger(ledgerUuid: String): List<TransactionEntity> {
        return transactionsQueries.getTransactionByUuid(
            ledgerUuid
        ).executeAsList()
    }

    fun getTransactionsBetween(startTimeStamp: Long, endTimeStamp: Long): List<TransactionEntity> {
        return transactionsQueries.getTransactionsBetweenTimeStamps(startTimeStamp, endTimeStamp)
            .executeAsList()
    }


    fun getTransactionsCount(): Int {
        return transactionsQueries.getTransactionsCount().executeAsOne().toInt()
    }


    fun upsertTransactionByUuid(transaction: TransactionEntity) {
        transactionsQueries.upsertTransactionByUuid(
            uuid = transaction.uuid,
            ledger_uuid = transaction.ledger_uuid,
            transaction_date = transaction.transaction_date,
            category_name = transaction.category_name,
            transaction_type = transaction.transaction_type,
            amount = transaction.amount,
            currency_code = transaction.currency_code,
            remark = transaction.remark,
            screenshot_uri = transaction.screenshot_uri,
        )
    }


    fun countTransactionsAfter(timeStamp: Long): Int {
        return transactionsQueries.countTransactionsUpdatedAfter(timeStamp).executeAsOne().toInt()
    }

    fun getTransactionsUpdatedAfter(timeStamp: Long): List<TransactionEntity> {
        return transactionsQueries.getTransactionsUpdatedAfter(timeStamp).executeAsList()
    }

    fun getTransactionByUuid(uuid: String): TransactionEntity {
        return transactionsQueries.getTransactionByUuid(uuid).executeAsOne()
    }
}
