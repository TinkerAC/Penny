package app.penny.core.domain.usecase

import app.penny.core.data.model.toLedgerDto
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.UploadLedgerResponse
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
            val response: UploadLedgerResponse =
                apiClient.pushLedgers(
                    ledgers = ledgers.map { it.toLedgerDto() },
                    lastSynced = lastSyncedAt.epochSeconds
                )


            //update last synced at using server response

            val newLastSyncedAt = Instant.fromEpochSeconds(response.lastSyncedAt)

            userDataRepository.setLastSyncedAt(newLastSyncedAt)



        } catch (e: Exception) {
            Logger.e("Failed to upload ${ledgers.size} ledgers", e)
        }

        Logger.d("Uploaded ${ledgers.size} ledgers")
    }
}