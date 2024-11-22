package app.penny.core.data.repository.impl

import app.penny.core.data.database.TransactionLocalDataSource
import app.penny.core.data.model.toEntity
import app.penny.core.data.model.toModel
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.model.TransactionModel
import kotlinx.datetime.Instant

class TransactionRepositoryImpl(

    private val localDataSource: TransactionLocalDataSource

) : TransactionRepository {
    override suspend fun getTransactionById(transactionId: Long): TransactionModel {
        return localDataSource.getTransactionById(transactionId).toModel()
    }

    override suspend fun getTransactionsBetween(
        startInstant: Instant,
        endInstant: Instant
    ): List<TransactionModel> {

        return localDataSource.getTransactionsBetween(
            startInstant.epochSeconds,
            endInstant.epochSeconds
        ).map { it.toModel() }
    }

    override suspend fun getAllTransactions(): List<TransactionModel> {
        return localDataSource.getAllTransactions().map { it.toModel() }
    }

    override suspend fun insertTransaction(transaction: TransactionModel) {
        localDataSource.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransactionById(transactionId: Long, transaction: TransactionModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTransactionById(transactionId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getTransactionsByLedger(ledgerId: Long): List<TransactionModel> {
        return localDataSource.getTransactionsByLedger(ledgerId).map { it.toModel() }
    }

    override suspend fun getTransactionsCount(): Int {
        return localDataSource.getTransactionsCount()
    }
}
