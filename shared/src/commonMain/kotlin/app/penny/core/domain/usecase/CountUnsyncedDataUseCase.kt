package app.penny.core.domain.usecase

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.network.ApiClient
import co.touchlab.kermit.Logger
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CountUnsyncedDataUseCase(
    private val ledgerRepository: LedgerRepository,
    private val transactionRepository: TransactionRepository,
    private val userDataRepository: UserDataRepository,
    private val apiClient: ApiClient
) {
    suspend operator fun invoke(): CountUnsyncedDataResult {

        val userUuid = userDataRepository.getUserUuid()

        val lastSyncedAt: Instant? = userDataRepository.getLastSyncedAt()

        val unsyncedLedgerCount = ledgerRepository.countByUserUuidAndUpdatedAtAfter(
            userUuid = userUuid,
            timeStamp = lastSyncedAt ?: Instant.DISTANT_PAST
        )


        val unsyncedTransactionCount = transactionRepository.countByUserUuidAndUpdatedAtAfter(
            userUuid = userUuid,
            timeStamp = lastSyncedAt ?: Instant.DISTANT_PAST
        )

        var unsyncedRemoteLedgerCount: Long
        var unsyncedRemoteTransactionCount: Long

        try {
            val unsyncedRemoteChangeCount = apiClient.sync.getRemoteUnsyncedDataCount(
                lastSyncedAt = lastSyncedAt?.epochSeconds ?: Instant.DISTANT_PAST.epochSeconds
            )
            unsyncedRemoteLedgerCount = unsyncedRemoteChangeCount.unsyncedLedgersCount

            unsyncedRemoteTransactionCount = unsyncedRemoteChangeCount.unsyncedTransactionsCount


        } catch (e: Exception) {
            Logger.w(e) { "Failed to get remote unsynced data count" }
            unsyncedRemoteLedgerCount = 0
            unsyncedRemoteTransactionCount = 0
        }

        return CountUnsyncedDataResult(
            unsyncedLocalLedgerCount = unsyncedLedgerCount.toLong(),
            unsyncedLocalTransactionCount = unsyncedTransactionCount.toLong(),
            unsyncedRemoteLedgerCount = unsyncedRemoteLedgerCount,
            unsyncedRemoteTransactionCount = unsyncedRemoteTransactionCount
        )


    }
}


data class CountUnsyncedDataResult(
    val unsyncedLocalLedgerCount: Long,
    val unsyncedLocalTransactionCount: Long,
    val unsyncedRemoteLedgerCount: Long = 0L,
    val unsyncedRemoteTransactionCount: Long = 0L

) {
    val hasUnsyncedData: Boolean
        get() = unsyncedLocalLedgerCount > 0 || unsyncedLocalTransactionCount > 0 || unsyncedRemoteLedgerCount > 0 || unsyncedRemoteTransactionCount > 0
}