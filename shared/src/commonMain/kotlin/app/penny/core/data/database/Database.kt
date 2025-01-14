package app.penny.core.data.database

import app.cash.sqldelight.db.SqlDriver
import app.penny.database.PennyDatabase


class Database(
    val driver: SqlDriver
) {
    private val database = PennyDatabase(driver)

    val transactionQueries = database.transactionsQueries
    val ledgerQueries = database.ledgerQueries
    val chatMessageQueries = database.chatMessageQueries

}
