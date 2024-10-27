package app.penny.data.datasource

import app.penny.database.TransactionEntity

interface TransactionLocalDataSource {
    suspend fun getTransactions(): List<TransactionEntity>

    suspend fun insertTransaction()
}