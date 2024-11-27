package app.penny.core.data.database

import app.penny.database.TransactionEntity
import app.penny.database.TransactionsQueries


class TransactionLocalDataSource(
    private val transactionsQueries: TransactionsQueries
) {

    fun insertTransaction(transaction: TransactionEntity) {
        transactionsQueries.insertTransaction(
            uuid = transaction.uuid,
            ledger_id = transaction.ledger_id,
            transaction_date = transaction.transaction_date,
            category_name = transaction.category_name,
            transaction_type = transaction.transaction_type,
            amount = transaction.amount,
            currency_code = transaction.currency_code,
            remark = transaction.remark,
            screenshot_uri = transaction.screenshot_uri,
        )
    }

    fun getTransactionById(id: Long): TransactionEntity {
        return transactionsQueries.getTransactionById(id).executeAsOne()
    }

    fun getAllTransactions(): List<TransactionEntity> {
        return transactionsQueries.getAllTransactions().executeAsList()
    }

    fun updateTransaction(transaction: TransactionEntity) {
        transactionsQueries.updateTransaction(
            ledger_id = transaction.ledger_id,
            transaction_date = transaction.transaction_date,
            category_name = transaction.category_name,
            transaction_type = transaction.transaction_type,
            amount = transaction.amount,
            currency_code = transaction.currency_code,
            remark = transaction.remark,
            screenshot_uri = transaction.screenshot_uri,
            id = transaction.id
        )
    }


    fun deleteTransaction(id: Long) {
        transactionsQueries.deleteTransaction(id)
    }

    fun getTransactionsByLedger(ledgerId: Long): List<TransactionEntity> {
        return transactionsQueries.getTransactionsByLedgerId(ledgerId).executeAsList()
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
            ledger_id = transaction.ledger_id,
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

}
