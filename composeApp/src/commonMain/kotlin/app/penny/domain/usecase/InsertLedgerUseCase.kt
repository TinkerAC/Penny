package app.penny.domain.usecase

import app.penny.data.repository.LedgerRepository
import app.penny.domain.enum.LedgerCover
import app.penny.domain.model.LedgerModel

class InsertLedgerUseCase(
    private val ledgerRepository: LedgerRepository,
) {

    suspend operator fun invoke(name: String, currencyCode: String, description: String,cover: LedgerCover) {
        ledgerRepository.insertLedger(
            LedgerModel(
                name =name,
                currencyCode = currencyCode,
                description = description,
                cover = cover
            )
        )
    }

}