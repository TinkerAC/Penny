package app.penny.core.domain.usecase

import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.enumerate.TransactionType
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.Summary
import app.penny.core.domain.model.TransactionModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi


class GetSummaryUseCase(
    private val transactionRepository: TransactionRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        ledger: LedgerModel,
        startInstant: Instant,
        endInstant: Instant
    ): Summary {
        val allTransactions: List<TransactionModel> =
            transactionRepository.findByLedgerAndTransactionDateBetween(
                ledgerUuid = ledger.uuid,
                startInstant = startInstant,
                endInstant = endInstant,
            )

        val totalIncome: BigDecimal =
            allTransactions.filter { it.transactionType == TransactionType.INCOME }
                .map { it.amount }
                .reduceOrNull { acc, bigDecimal -> acc + bigDecimal } ?: BigDecimal.ZERO


        val totalExpense: BigDecimal =
            allTransactions.filter { it.transactionType == TransactionType.EXPENSE }
                .map { it.amount }
                .reduceOrNull { acc, bigDecimal -> acc + bigDecimal } ?: BigDecimal.ZERO


        return Summary(
            income = totalIncome,
            expense = totalExpense,
            balance = totalIncome - totalExpense
        )

    }
}