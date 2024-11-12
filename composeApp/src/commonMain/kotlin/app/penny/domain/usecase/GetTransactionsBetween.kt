package app.penny.domain.usecase

import app.penny.data.repository.TransactionRepository
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
        transactionRepository.getTransactionsBetween(
            startInstant = startInstant,
            endInstant = endInstant
        )
    }
}