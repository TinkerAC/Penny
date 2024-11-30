package app.penny.core.data.repository


import app.penny.core.domain.model.TransactionModel
import app.penny.servershared.dto.DownloadTransactionResponse
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface TransactionRepository {

    suspend fun findByUuid(transactionUuid: Uuid): TransactionModel?
    suspend fun findAll(): List<TransactionModel>
    suspend fun insert(transaction: TransactionModel)
    suspend fun updateByUuid(transactionUuid: Uuid, transaction: TransactionModel)
    suspend fun deleteByUuid(transactionUuid: Uuid)
    suspend fun findByLedgerUuid(ledgerUuid: Uuid): List<TransactionModel>
    suspend fun findByLedgerUuidAndUpdatedAtBetween(
        ledgerUuid: Uuid,
        startInstant: Instant,
        endInstant: Instant
    ): List<TransactionModel>


    suspend fun findByUserUuidAndUpdatedAtAfter(
        userUuid: Uuid,
        timeStamp: Instant
    ): List<TransactionModel>

    suspend fun upsert(transaction: TransactionModel)


    suspend fun count(): Long


    suspend fun countByLedgerUuid(ledgerUuid: Uuid): Long
    suspend fun countByUserUuidAndUpdatedAtAfter(userUuid: Uuid, timeStamp: Instant): Long


    suspend fun uploadUnsyncedTransactions(
        transactions: List<TransactionModel>,
        lastSyncedAt: Instant
    )


    suspend fun downloadUnsyncedTransactions(
        lastSyncedAt: Instant
    ): DownloadTransactionResponse


}