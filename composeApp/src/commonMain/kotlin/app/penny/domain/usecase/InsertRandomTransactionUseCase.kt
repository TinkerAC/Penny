package app.penny.domain.usecase

import app.penny.data.repository.LedgerRepository
import app.penny.data.repository.TransactionRepository
import app.penny.domain.enum.Category
import app.penny.domain.enum.TransactionType
import app.penny.domain.model.TransactionModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlin.math.round
import kotlin.random.Random

class InsertRandomTransactionUseCase(
    private val ledgerRepository: LedgerRepository,
    private val transactionRepository: TransactionRepository

) {

    suspend operator fun invoke(
        count: Int,
    ) {

        val ledgers = ledgerRepository.getAllLedgers()
        val random = Random.Default
        val transactionTypes = TransactionType.entries.toTypedArray()


        repeat(count) {
            val amount = BigDecimal.fromDouble(round(random.nextDouble(0.0, 1000.0) * 100) / 100)
            val transactionDate =
                Random.nextLong(1514736000L, 1735488000L) // 2018-01-01 ~ 2025-01-01
            val ledger = ledgers.random()
            val transactionType = transactionTypes.random()

            val category = when (transactionType) {
                TransactionType.EXPENSE -> Category.getSubCategories(
                    Category.getSubCategories(
                        Category.EXPENSE
                    ).random()
                ).random()

                TransactionType.INCOME -> Category.getSubCategories(
                    Category.getSubCategories(
                        Category.INCOME
                    ).random()
                ).random()
            }


            transactionRepository.insertTransaction(
                TransactionModel(
                    transactionDate = Instant.fromEpochSeconds(transactionDate),
                    amount = amount,
                    category = category,
                    ledgerId = ledger.id,
                    currency = ledger.currency,
                    transactionType = transactionType

                )
            )


        }


    }

}