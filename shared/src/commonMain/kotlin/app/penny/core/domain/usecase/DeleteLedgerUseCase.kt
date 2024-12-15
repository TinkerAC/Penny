package app.penny.core.domain.usecase

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import kotlin.uuid.ExperimentalUuidApi

class DeleteLedgerUseCase(
    private val userDataRepository: UserDataRepository,
    private val ledgerRepository: LedgerRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke() {
        val userUuid = userDataRepository.getUserUuid()

        if (ledgerRepository.countByUserUuid(userUuid) == 1L) {
            throw IllegalStateException("Cannot delete the only ledger")
        }
        ledgerRepository.deleteByUuid(userUuid)
    }


}