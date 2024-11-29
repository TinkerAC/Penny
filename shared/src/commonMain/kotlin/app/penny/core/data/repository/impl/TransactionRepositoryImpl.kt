package app.penny.core.data.repository.impl

import app.penny.core.data.database.LedgerLocalDataSource
import app.penny.core.data.database.TransactionLocalDataSource
import app.penny.core.data.model.toEntity
import app.penny.core.data.model.toLedgerModel
import app.penny.core.data.model.toTransactionDto
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.model.TransactionModel
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.DownloadTransactionResponse
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
class TransactionRepositoryImpl(

    private val transactionLocalDataSource: TransactionLocalDataSource,
    private val ledgerLocalDataSource: LedgerLocalDataSource,
    private val apiClient: ApiClient

) : TransactionRepository {
    override suspend fun findByUuid(transactionUuid: Uuid): TransactionModel {

        return transactionLocalDataSource.getTransactionByUuid(
            transactionUuid.toString()
        ).toLedgerModel()
    }

    override suspend fun findByUpdatedAtBetween(
        startInstant: Instant, endInstant: Instant
    ): List<TransactionModel> {

        return transactionLocalDataSource.getTransactionsBetween(
            startInstant.epochSeconds, endInstant.epochSeconds
        ).map { it.toLedgerModel() }
    }

    override suspend fun findAll(): List<TransactionModel> {
        return transactionLocalDataSource.getAllTransactions().map { it.toLedgerModel() }
    }

    override suspend fun insert(transaction: TransactionModel) {

        transaction.uuid = Uuid.random()

        transactionLocalDataSource.insertTransaction(
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
        return transactionLocalDataSource.getTransactionsByLedger(ledgerUuid.toString())
            .map { it.toLedgerModel() }
    }

    override suspend fun count(): Int {
        return transactionLocalDataSource.getTransactionsCount()


    }

    override suspend fun upsert(transaction: TransactionModel) {
        transactionLocalDataSource.upsertTransactionByUuid(transaction.toEntity())
    }

    override suspend fun countByUpdatedAtAfter(timeStamp: Instant): Int {
        return transactionLocalDataSource.countTransactionsAfter(timeStamp.epochSeconds)
    }

    override suspend fun findByUpdatedAtAfter(timeStamp: Instant): List<TransactionModel> {
        return transactionLocalDataSource.getTransactionsUpdatedAfter(timeStamp.epochSeconds)
            .map { it.toLedgerModel() }
    }


    override suspend fun uploadUnsyncedTransactions(
        transactions: List<TransactionModel>, lastSyncedAt: Instant
    ) {

        apiClient.sync.uploadTransactions(
            transactions = transactions.map {
                it.toTransactionDto(
                    //find uuid of ledger
                    ledgerUuid = Uuid.parse(
                        ledgerLocalDataSource.getLedgerByUuid(
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
}
