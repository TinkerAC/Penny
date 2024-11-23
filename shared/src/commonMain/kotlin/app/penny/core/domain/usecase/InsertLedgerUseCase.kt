package app.penny.core.domain.usecase

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.domain.enum.Currency
import app.penny.core.domain.enum.LedgerCover
import app.penny.core.domain.model.LedgerModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class InsertLedgerUseCase(
    private val ledgerRepository: LedgerRepository,
) {

    suspend operator fun invoke(
        name: String,
        currency: Currency,
        description: String,
        cover: LedgerCover
    ) {
        ledgerRepository.insertLedger(
            LedgerModel(
                name = name,
                currency = currency,
                description = description,
                cover = cover
            )
        )
    }

}