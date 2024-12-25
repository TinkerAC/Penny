package app.penny.core.data.repository.impl

import app.penny.core.data.database.LedgerLocalDataSource
import app.penny.core.data.database.TransactionLocalDataSource
import app.penny.core.data.model.toDto
import app.penny.core.data.model.toEntity
import app.penny.core.data.model.toModel
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserModel
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.responseDto.DownloadTransactionResponse
import co.touchlab.kermit.Logger
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
class TransactionRepositoryImpl(
    private val transactionLocalDataSource: TransactionLocalDataSource,
    private val ledgerLocalDataSource: LedgerLocalDataSource,
    private val apiClient: ApiClient
) : TransactionRepository {
    override suspend fun findByUuid(transactionUuid: Uuid): TransactionModel? {
        return transactionLocalDataSource.findByUuid(transactionUuid.toString())?.toModel()
    }

    override suspend fun findByLedgerUuidAndUpdatedAtBetween(
        ledgerUuid: Uuid, startInstant: Instant, endInstant: Instant
    ): List<TransactionModel> {

        return transactionLocalDataSource.findByLedgerUuidAndTransactionDateBetween(
            ledgerUuid.toString(), startInstant.epochSeconds, endInstant.epochSeconds
        ).map { it.toModel() }


    }

    override suspend fun findAll(): List<TransactionModel> {
        return transactionLocalDataSource.findAll().map { it.toModel() }
    }

    override suspend fun insert(transaction: TransactionModel) {

        transaction.uuid = Uuid.random()


        transactionLocalDataSource.insert(
            transaction.toEntity()
        )
    }

    override suspend fun update(transaction: TransactionModel) {
        transactionLocalDataSource.updateByUuid(transaction.toEntity())
    }

    override suspend fun deleteByUuid(transactionUuid: Uuid) {
        transactionLocalDataSource.deleteByUuid(transactionUuid.toString())
    }

    override suspend fun findByLedgerUuid(ledgerUuid: Uuid): List<TransactionModel> {
        return transactionLocalDataSource.findByLedgerUuid(ledgerUuid.toString())
            .map { it.toModel() }
    }

    override suspend fun count(): Long {
        return transactionLocalDataSource.count()

    }

    override suspend fun upsert(transaction: TransactionModel) {
        transactionLocalDataSource.upsertByUuid(transaction.toEntity())
    }


    override suspend fun findByUserUuidAndUpdatedAtAfter(
        userUuid: Uuid,
        timeStamp: Instant
    ): List<TransactionModel> {
        return transactionLocalDataSource.findByUserUuidAndUpdatedAtAfter(
            userUuid = userUuid.toString(),
            timestamp = timeStamp.epochSeconds
        )
            .map { it.toModel() }
    }


    override suspend fun uploadUnsyncedTransactions(
        transactions: List<TransactionModel>, lastSyncedAt: Instant
    ) {

        apiClient.sync.uploadTransactions(
            transactions = transactions.map {
                it.toDto(
                    //find uuid of ledger
                    ledgerUuid = Uuid.parse(
                        ledgerLocalDataSource.findByUuid(
                            it.ledgerUuid.toString()
                        )!!.uuid
                    )

                )
            },
            lastSynced = lastSyncedAt.epochSeconds,
        )
    }


    override suspend fun downloadUnsyncedTransactions(lastSyncedAt: Instant): DownloadTransactionResponse {
        val response = apiClient.sync.downloadTransactions(lastSyncedAt.epochSeconds)
        return response


    }

    override suspend fun countByLedgerUuid(ledgerUuid: Uuid): Long {
        return transactionLocalDataSource.countByLedgerUuid(ledgerUuid.toString())
    }

    override suspend fun countByUserUuidAndUpdatedAtAfter(
        userUuid: Uuid,
        timeStamp: Instant
    ): Long {
        return transactionLocalDataSource.countByUserUuidAndUpdatedAtAfter(
            userUuid = userUuid.toString(),
            timestamp = timeStamp.epochSeconds
        )
    }


    override suspend fun findByUser(user: UserModel): List<TransactionModel> {
        return transactionLocalDataSource.findByUserUuid(user.uuid.toString())
            .map { it.toModel() }
    }


    override suspend fun findByUserAndYearMonth(
        userUuid: Uuid,
        yearMonth: YearMonth
    ): List<TransactionModel> {
        val startEpochSeconds = yearMonth.getStartEpochSeconds()
        val endEpochSeconds = yearMonth.getEndEpochSeconds()
        // 调用 LocalDataSource 查询
        return transactionLocalDataSource.findByUserUuidAndTransactionDateBetween(
            userUuid.toString(), startEpochSeconds, endEpochSeconds
        ).map { it.toModel() }
    }


    override suspend fun findRecentByLedger(
        ledger: LedgerModel,
        limit: Long
    ): List<TransactionModel> {
        Logger.d("findRecentByLedger: $ledger")
        return transactionLocalDataSource.findRecentByLedgerUuid(ledger.uuid.toString(), limit)
            .map { it.toModel() }
    }

    override suspend fun findByLedgerAndTransactionDateBetween(
        ledgerUuid: Uuid,
        startInstant: Instant,
        endInstant: Instant
    ): List<TransactionModel> {
        val result = transactionLocalDataSource.findByLedgerUuidAndTransactionDateBetween(
            ledgerUuid.toString(), startInstant.epochSeconds, endInstant.epochSeconds
        ).map {
            it.toModel()
        }

        return result
    }
}
