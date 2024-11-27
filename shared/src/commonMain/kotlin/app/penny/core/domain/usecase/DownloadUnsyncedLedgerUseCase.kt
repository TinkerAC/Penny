package app.penny.core.domain.usecase

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import kotlinx.datetime.Instant

class DownloadUnsyncedLedgerUseCase(
    private val ledgerRepository: LedgerRepository,
    private val userDataRepository: UserDataRepository

) {
    suspend operator fun invoke() {

        val lastSyncedAt :Instant? = userDataRepository.getLastSyncedAt()

        val unsyncedLedgers = ledgerRepository.downloadUnsyncedLedgers(
             lastSyncedAt = lastSyncedAt?: Instant.DISTANT_PAST
        )

        //call upsertLedgers for each ledger

        unsyncedLedgers.forEach {
            ledgerRepository.upsertLedger(it)
        }







    }
}