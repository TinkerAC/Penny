package app.penny.domain.usecase

import androidx.compose.foundation.interaction.DragInteraction
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