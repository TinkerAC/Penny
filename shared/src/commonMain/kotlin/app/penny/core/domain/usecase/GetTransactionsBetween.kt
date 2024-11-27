package app.penny.core.domain.usecase

import app.penny.core.data.repository.TransactionRepository
import kotlinx.datetime.Instant

/**
 *  GetTransactionsBetween
 *  @param transactionRepository
 *  @constructor Create empty Get transactions between
 *
 *  get transactions between start instant and end instant
 *
 */


class GetTransactionsBetween(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(startInstant: Instant, endInstant: Instant) {
        transactionRepository.findTransactionsBetween(
            startInstant = startInstant,
            endInstant = endInstant
        )
    }
}