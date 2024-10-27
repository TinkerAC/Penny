package app.penny.data.datasource

import app.penny.data.model.TransactionType
import app.penny.database.TransactionEntity
import kotlinx.datetime.Clock


class TransactionLocalDataSourceImpl(private val database: Database) :
    TransactionLocalDataSource {
    override suspend fun getTransactions(): List<TransactionEntity> {
        return database.transactionQueries.selectAll().executeAsList().map {
            TransactionEntity(
                transaction_id = it.transaction_id,
                ledger_id = it.ledger_id,
                transaction_date = it.transaction_date,
                category_id = it.category_id,
                transaction_type = TransactionType.valueOf(it.transaction_type),
                amount = it.amount,
                currency_code = it.currency_code,
                content = it.content,
                screenshot_uri = it.screenshot_uri,
                note = it.note,
                created_at = it.created_at,
                updated_at = it.updated_at

            )
        }
    }


    override suspend fun insertTransaction() {
        database.transactionQueries.insertTransaction(
            ledger_id = 1,
            transaction_date = Clock.System.now().toEpochMilliseconds(),
            category_id = 1,
            transaction_type = TransactionType.EXPENSE,
            amount = 1000,
            currency_code = "CNY",
            content = "Test",
            screenshot_uri = "Test",
            note = "Test",
        )

    }
}
