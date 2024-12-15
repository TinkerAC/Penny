package app.penny.core.data.repository.impl

import app.penny.core.data.database.LedgerLocalDataSource
import app.penny.core.data.database.TransactionLocalDataSource
import app.penny.core.data.model.toDto
import app.penny.core.data.model.toEntity
import app.penny.core.data.model.toModel
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.responseDto.DownloadTransactionResponse
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
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

    override suspend fun updateByUuid(transactionUuid: Uuid, transaction: TransactionModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByUuid(transactionUuid: Uuid) {
        TODO("Not yet implemented")
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


    override suspend fun findByUserUuid(userUuid: Uuid): List<TransactionModel> {
        return transactionLocalDataSource.findByUserUuid(userUuid.toString())
            .map { it.toModel() }
    }


    override suspend fun findByUserUuidAndYearMonth(
        userUuid: Uuid,
        yearMonth: YearMonth
    ): List<TransactionModel> {
        // 指定时区，这里以系统默认时区为例
        val timeZone = TimeZone.currentSystemDefault()

        // 创建该月的第一天的 LocalDateTime（00:00:00）
        val startDateTime = LocalDateTime(yearMonth.year, yearMonth.month, 1, 0, 0, 0)
        // 转换为 Instant
        val startInstant = startDateTime.toInstant(timeZone)
        // 获取 epochSeconds
        val startEpochSeconds = startInstant.epochSeconds

        // 获取该月的最后一天
        val lastDay = LocalDate(yearMonth.year, yearMonth.month, 1).dayOfMonth
        // 创建该月最后一天的 LocalDateTime（23:59:59）
        val endDateTime = LocalDateTime(yearMonth.year, yearMonth.month, lastDay, 23, 59, 59)
        // 转换为 Instant
        val endInstant = endDateTime.toInstant(timeZone)
        // 获取 epochSeconds
        val endEpochSeconds = endInstant.epochSeconds

        // 调用 DAO 方法查询该时间区间内的交易记录
        return transactionLocalDataSource.findByUserUuidAndTransactionDateBetween(
            userUuid.toString(), startEpochSeconds, endEpochSeconds
        ).map { it.toModel() }
    }
}
