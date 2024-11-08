package app.penny.data.repository.impl

import app.penny.data.datasource.local.TransactionLocalDataSource
import app.penny.data.model.toEntity
import app.penny.data.model.toModel
import app.penny.data.repository.TransactionRepository
import app.penny.domain.model.TransactionModel

class TransactionRepositoryImpl(

    private val localDataSource: TransactionLocalDataSource

) : TransactionRepository {
    override suspend fun getTransactionById(transactionId: Long): TransactionModel {
        return localDataSource.getTransactionById(transactionId).toModel()


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

    override suspend fun getTransactionsByLedger(ledgerId:Long): List<TransactionModel> {
        return localDataSource.getTransactionsByLedger(ledgerId).map { it.toModel() }
    }
}
