package app.penny.data.repository


import app.penny.domain.model.TransactionModel

interface TransactionRepository {

    suspend fun getTransactionById(transactionId: Long): TransactionModel
    suspend fun getAllTransactions(): List<TransactionModel>
    suspend fun insertTransaction(transaction: TransactionModel)
    suspend fun updateTransactionById(transactionId: Long, transaction: TransactionModel)
    suspend fun deleteTransactionById(transactionId: Long)

}