package app.penny.core.domain.usecase

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import kotlinx.datetime.Instant

class UploadUpdatedLedgersUseCase(
    private val userDataRepository: UserDataRepository,
    private val ledgerRepository: LedgerRepository
) {
    suspend operator fun invoke() {

        //get last synced at
        val lastSyncedAt :Instant = userDataRepository.getLastSyncedAt()

        //get all ledgers updated after last synced at

        val ledgers = ledgerRepository.getLedgersUpdatedAfter(
            lastSyncedAt
        )





    }
}