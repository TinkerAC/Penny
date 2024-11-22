package app.penny.core.data.repository


import app.penny.core.domain.model.TransactionModel
import kotlinx.datetime.Instant

interface TransactionRepository {

    suspend fun getTransactionById(transactionId: Long): TransactionModel
    suspend fun getAllTransactions(): List<TransactionModel>
    suspend fun insertTransaction(transaction: TransactionModel)
    suspend fun updateTransactionById(transactionId: Long, transaction: TransactionModel)
    suspend fun deleteTransactionById(transactionId: Long)
    suspend fun getTransactionsByLedger(ledgerId: Long): List<TransactionModel>
    suspend fun getTransactionsBetween(
        startInstant: Instant,
        endInstant: Instant
    ): List<TransactionModel>


    suspend fun getTransactionsCount(): Int

}