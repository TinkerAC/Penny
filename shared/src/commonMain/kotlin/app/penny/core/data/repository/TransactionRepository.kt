package app.penny.core.data.repository


import app.penny.core.domain.model.TransactionModel
import app.penny.servershared.dto.DownloadLedgerResponse
import app.penny.servershared.dto.DownloadTransactionResponse
import kotlinx.datetime.Instant

interface TransactionRepository {

    suspend fun findTransactionById(transactionId: Long): TransactionModel
    suspend fun findAllTransactions(): List<TransactionModel>
    suspend fun addTransaction(transaction: TransactionModel)
    suspend fun updateTransactionById(transactionId: Long, transaction: TransactionModel)
    suspend fun deleteTransactionById(transactionId: Long)
    suspend fun findTransactionsByLedger(ledgerId: Long): List<TransactionModel>
    suspend fun findTransactionsBetween(
        startInstant: Instant,
        endInstant: Instant
    ): List<TransactionModel>


    suspend fun findTransactionsUpdatedAfter(timeStamp: Instant): List<TransactionModel>


    suspend fun upsertTransaction(transaction: TransactionModel)


    suspend fun countTransactionsUpdatedAfter(timeStamp: Instant): Int


    suspend fun getTransactionsCount(): Int


    suspend fun uploadUnsyncedTransactions(
        transactions: List<TransactionModel>,
        lastSyncedAt: Instant
    )


    suspend fun downloadUnsyncedTransactions(
        lastSyncedAt: Instant
    ): DownloadTransactionResponse


}