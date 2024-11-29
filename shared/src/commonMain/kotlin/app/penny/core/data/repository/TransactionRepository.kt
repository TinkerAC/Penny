package app.penny.core.data.repository


import app.penny.core.domain.model.TransactionModel
import app.penny.servershared.dto.DownloadTransactionResponse
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface TransactionRepository {

    suspend fun findByUuid(transactionUuid: Uuid): TransactionModel
    suspend fun findAll(): List<TransactionModel>
    suspend fun insert(transaction: TransactionModel)
    suspend fun updateByUuid(transactionUuid: Uuid, transaction: TransactionModel)
    suspend fun deleteByUuid(transactionUuid: Uuid)
    suspend fun findByLedgerUuid(ledgerUuid: Uuid): List<TransactionModel>
    suspend fun findByUpdatedAtBetween(
        startInstant: Instant,
        endInstant: Instant
    ): List<TransactionModel>


    suspend fun findByUpdatedAtAfter(timeStamp: Instant): List<TransactionModel>


    suspend fun upsert(transaction: TransactionModel)


    suspend fun countByUpdatedAtAfter(timeStamp: Instant): Int


    suspend fun count(): Int


    suspend fun uploadUnsyncedTransactions(
        transactions: List<TransactionModel>,
        lastSyncedAt: Instant
    )


    suspend fun downloadUnsyncedTransactions(
        lastSyncedAt: Instant
    ): DownloadTransactionResponse


}