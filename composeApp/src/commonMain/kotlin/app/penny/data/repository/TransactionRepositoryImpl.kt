package app.penny.data.repository


//import app.penny.data.datasource.TransactionRemoteDataSource //未实现
import app.penny.data.datasource.TransactionLocalDataSource
import app.penny.data.model.toDomainModel
import app.penny.domain.model.Transaction

class TransactionRepositoryImpl(
//    private val remoteDataSource: TransactionRemoteDataSource,
    private val localDataSource: TransactionLocalDataSource
) : TransactionRepository {
    override suspend fun getTransactions(): List<Transaction> {
        val localTransactions = localDataSource.getTransactions()
//        return if (localTransactions.isNotEmpty()) {
//            localTransactions.map { it.toDomainModel() }
//        } else {
//            val remoteTransactions = remoteDataSource.fetchTransactions()
//            localDataSource.saveTransactions(remoteTransactions.map { it.toEntity() })
//            remoteTransactions.map { it.toDomainModel() }
//        }

        return localTransactions.map { it.toDomainModel() }
    }

    override suspend fun insertTransaction() {
        localDataSource.insertTransaction()
    }
}
