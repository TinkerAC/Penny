package app.penny.core.domain.usecase

import app.penny.core.data.model.toModel
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import co.touchlab.kermit.Logger
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class SyncDataUseCase(
    private val countUnsyncedDataUseCase: CountUnsyncedDataUseCase,
    private val userDataRepository: UserDataRepository,
    private val ledgerRepository: LedgerRepository,
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke() {

        val user= userDataRepository.getUser()

        val countUnsyncedDataResult = countUnsyncedDataUseCase()

        val lastSyncedAt: Instant = userDataRepository.getLastSyncedAt() ?: Instant.DISTANT_PAST

        if (countUnsyncedDataResult.hasUnsyncedData) {
            try {
                //1.Prepare local changes
                val ledgerLocalChanges: List<LedgerModel> =
                    ledgerRepository.findByUserUuidAndUpdatedAtAfter(
                        userUuid = user.uuid,
                        timeStamp = lastSyncedAt
                    )
                val transactionLocalChanges: List<TransactionModel> =
                    transactionRepository.findByUserUuidAndUpdatedAtAfter(
                        userUuid = user.uuid,
                        timeStamp = lastSyncedAt
                    )

                //2. Upload local changes(server do upsert)

                try {
                    ledgerRepository.uploadUnsyncedLedgersByUserUuid(
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
                    val remoteLedgers = ledgerRepository.downloadUnsyncedLedgersByUserUuid(
                        lastSyncedAt = lastSyncedAt
                    )

                    var updatedLedgers = 0;
                    var insertedLedgers = 0;

                    // insert or update remote ledgers
                    remoteLedgers.forEach {
                        val insertedNewLedger = ledgerRepository.upsert(it)
                        if (insertedNewLedger) {
                            updatedLedgers++
                        } else {
                            insertedLedgers++
                        }
                    }

                    Logger.d("Updated ledgers: $updatedLedgers, Inserted ledgers: $insertedLedgers")


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
                        transactionRepository.upsert(it.toModel())
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