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
    override suspend fun findTransactionById(transactionId: Long): TransactionModel {
        return transactionLocalDataSource.getTransactionById(transactionId).toLedgerModel()
    }

    override suspend fun findTransactionsBetween(
        startInstant: Instant, endInstant: Instant
    ): List<TransactionModel> {

        return transactionLocalDataSource.getTransactionsBetween(
            startInstant.epochSeconds, endInstant.epochSeconds
        ).map { it.toLedgerModel() }
    }

    override suspend fun findAllTransactions(): List<TransactionModel> {
        return transactionLocalDataSource.getAllTransactions().map { it.toLedgerModel() }
    }

    override suspend fun addTransaction(transaction: TransactionModel) {

        transaction.uuid = Uuid.random()

        transactionLocalDataSource.insertTransaction(
            transaction.toEntity()
        )
    }

    override suspend fun updateTransactionById(transactionId: Long, transaction: TransactionModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTransactionById(transactionId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun findTransactionsByLedger(ledgerId: Long): List<TransactionModel> {
        return transactionLocalDataSource.getTransactionsByLedger(ledgerId)
            .map { it.toLedgerModel() }
    }

    override suspend fun getTransactionsCount(): Int {
        return transactionLocalDataSource.getTransactionsCount()


    }

    override suspend fun upsertTransaction(transaction: TransactionModel) {
        transactionLocalDataSource.upsertTransactionByUuid(transaction.toEntity())
    }

    override suspend fun countTransactionsUpdatedAfter(timeStamp: Instant): Int {
        return transactionLocalDataSource.countTransactionsAfter(timeStamp.epochSeconds)
    }

    override suspend fun findTransactionsUpdatedAfter(timeStamp: Instant): List<TransactionModel> {
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
                        ledgerLocalDataSource.getLedgerById(it.ledgerId).uuid
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
