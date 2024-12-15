package app.penny.core.domain.usecase

import kotlinx.datetime.Instant

class GetFilterTransactionsUseCase {
    suspend operator fun invoke(
        ledgerId: Long,
        startInstant: Instant,
        endInstant: Instant
    ) {
        throw NotImplementedError("Not implemented")
    }
}