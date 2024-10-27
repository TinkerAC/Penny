package app.penny.data.repository


import app.penny.domain.model.Transaction

interface TransactionRepository {
    suspend fun getTransactions(): List<Transaction>
    suspend fun insertTransaction()

}