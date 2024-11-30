package app.penny.core.domain.usecase

import app.penny.core.data.repository.TransactionRepository
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 *  GetTransactionsBetween
 *  @param transactionRepository
 *  @constructor Create empty Get transactions between
 *
 *  get transactions between start instant and end instant
 *
 */

@OptIn(ExperimentalUuidApi::class)
class GetTransactionsBetween(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(ledgerUuid: Uuid, startInstant: Instant, endInstant: Instant) {
        transactionRepository.findByLedgerUuidAndUpdatedAtBetween(
            ledgerUuid = ledgerUuid,
            startInstant = startInstant,
            endInstant = endInstant
        )
    }
}