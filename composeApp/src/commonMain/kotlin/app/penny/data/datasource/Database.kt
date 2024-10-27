package app.penny.data.datasource

import app.cash.sqldelight.db.SqlDriver
import app.penny.data.datasource.DatabaseDriverFactory
import app.penny.database.PennyDatabase


class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val driver: SqlDriver = databaseDriverFactory.createDriver()
    private val database = PennyDatabase(driver)

    val transactionQueries = database.transactionsQueries
    val ledgerQueries = database.ledgerQueries
}
