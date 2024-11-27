package app.penny.core.domain.usecase

import app.penny.core.data.model.toTransactionModel
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class SyncDataUseCase(
    private val countUnsyncedDataUseCase: CountUnsyncedDataUseCase,
    private val userDataRepository: UserDataRepository,
    private val ledgerRepository: LedgerRepository,
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke() {

        val countUnsyncedDataResult = countUnsyncedDataUseCase()

        val lastSyncedAt: Instant = userDataRepository.getLastSyncedAt() ?: Instant.DISTANT_PAST

        if (countUnsyncedDataResult.hasUnsyncedData) {
            try {


                //1.Prepare local changes
                val ledgerLocalChanges: List<LedgerModel> =
                    ledgerRepository.findLedgersUpdatedAfter(
                        timeStamp = lastSyncedAt
                    )

                val transactionLocalChanges: List<TransactionModel> =
                    transactionRepository.findTransactionsUpdatedAfter(
                        timeStamp = lastSyncedAt
                    )


                //2. Upload local changes

                try {
                    ledgerRepository.uploadUnsyncedLedgers(
                        ledgers = ledgerLocalChanges,
                        lastSyncedAt = lastSyncedAt
                    )
                } catch (e: Exception) {
                    // Handle error
                }

                try {
                    transactionRepository.uploadUnsyncedTransactions(
                        transactions = transactionLocalChanges,
                        lastSyncedAt = lastSyncedAt
                    )
                } catch (e: Exception) {
                    // Handle error
                }


                //3.pull and apply remote changes


                try {
                    val remoteLedgers = ledgerRepository.downloadUnsyncedLedgers(
                        lastSyncedAt = lastSyncedAt
                    )

                    // insert or update remote ledgers
                    remoteLedgers.forEach {
                        ledgerRepository.upsertLedger(it)
                    }
                } catch (e: Exception) {
                    // Handle error
                }


                var newLastSyncedAt: Instant? = null

                try {
                    val response = transactionRepository.downloadUnsyncedTransactions(
                        lastSyncedAt = lastSyncedAt
                    )

                    newLastSyncedAt = Instant.fromEpochSeconds(response.lastSyncedAt)

                    // insert or update remote transactions
                    response.transactions.forEach {
                        transactionRepository.upsertTransaction(it.toTransactionModel())
                    }

                } catch (e: Exception) {
                    // Handle error
                }


                //4. Update lastSyncedAt if everything is successful (server time)

                if (newLastSyncedAt != null) {
                    userDataRepository.setLastSyncedAt(newLastSyncedAt)
                }


            } catch (e: Exception) {
                // Handle error
            } finally {

            }
        }
    }
}