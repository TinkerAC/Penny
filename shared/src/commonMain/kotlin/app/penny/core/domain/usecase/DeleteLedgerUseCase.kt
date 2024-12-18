package app.penny.core.domain.usecase

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.model.LedgerModel
import kotlin.uuid.ExperimentalUuidApi

class DeleteLedgerUseCase(
    private val userDataRepository: UserDataRepository,
    private val ledgerRepository: LedgerRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(ledger: LedgerModel) {
        val user = userDataRepository.getUser()

        if (ledgerRepository.countByUserUuid(user.uuid) == 1L) {
            throw IllegalStateException("Cannot delete the only ledger")
        }
        ledgerRepository.deleteByUuid(
            ledgerUuid = ledger.uuid
        )
    }


}