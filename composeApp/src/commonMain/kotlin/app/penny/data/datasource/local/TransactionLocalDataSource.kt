package app.penny.data.datasource.local

import app.penny.database.TransactionEntity
import app.penny.database.TransactionsQueries


class TransactionLocalDataSource(
    private val transactionsQueries: TransactionsQueries
) {

    fun insertTransaction(transaction: TransactionEntity) {
        transactionsQueries.insertTransaction(
            ledger_id = transaction.ledger_id,
            transaction_date = transaction.transaction_date,
            category_id = transaction.category_id,
            transaction_type = transaction.transaction_type,
            amount = transaction.amount,
            currency_code = transaction.currency_code,
            content = transaction.content,
            screenshot_uri = transaction.screenshot_uri,
            note = transaction.note,
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
            category_id = transaction.category_id,
            transaction_type = transaction.transaction_type,
            amount = transaction.amount,
            currency_code = transaction.currency_code,
            content = transaction.content,
            screenshot_uri = transaction.screenshot_uri,
            note = transaction.note,
            id = transaction.id
        )
    }


    fun deleteTransaction(id: Long) {
        transactionsQueries.deleteTransaction(id)
    }


}
