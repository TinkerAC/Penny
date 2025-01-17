package app.penny.core.data.repository


import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserModel
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.servershared.dto.responseDto.DownloadTransactionResponse
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface TransactionRepository {

    suspend fun findByUuid(transactionUuid: Uuid): TransactionModel?
    suspend fun findAll(): List<TransactionModel>
    suspend fun insert(transaction: TransactionModel)
    suspend fun update(transaction: TransactionModel)
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


    suspend fun findByUser(user: UserModel): List<TransactionModel>


    suspend fun findByUserAndYearMonth(
        userUuid: Uuid,
        yearMonth: YearMonth
    ): List<TransactionModel>

    suspend fun findRecentByLedger(
        ledger: LedgerModel,
        limit: Long
    ): List<TransactionModel>


    suspend fun findByLedgerAndTransactionDateBetween(
        ledgerUuid: Uuid,
        startInstant: Instant,
        endInstant: Instant
    ): List<TransactionModel>


    suspend fun findByLedgerAndYearMonth(
        ledger: LedgerModel,
        yearMonth: YearMonth
    ): List<TransactionModel>


}