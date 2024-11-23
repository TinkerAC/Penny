package app.penny.core.domain.usecase

import app.penny.core.data.model.toLedgerDto
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.network.ApiClient
import co.touchlab.kermit.Logger
import kotlinx.datetime.Instant

class UploadUpdatedLedgersUseCase(
    private val userDataRepository: UserDataRepository,
    private val ledgerRepository: LedgerRepository,
    private val apiClient: ApiClient
) {
    suspend operator fun invoke() {

        //get last synced at
        val lastSyncedAt: Instant = userDataRepository.getLastSyncedAt()

        //get all ledgers updated after last synced at

        val ledgers = ledgerRepository.getLedgersUpdatedAfter(
            lastSyncedAt
        )

        try {
            //upload ledgers
            apiClient.pushLedgers(
                ledgers.map { it.toLedgerDto() })
        } catch (e: Exception) {
            Logger.e("Failed to upload ${ledgers.size} ledgers", e)
        }

        Logger.d("Uploaded ${ledgers.size} ledgers")
    }
}