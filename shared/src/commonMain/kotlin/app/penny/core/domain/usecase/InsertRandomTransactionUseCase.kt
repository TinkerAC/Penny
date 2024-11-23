package app.penny.core.domain.usecase

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.TransactionModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlin.math.round
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
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

        //为每一天生成随机交易
        for (timeStamp in 1514736000L..1735488000L step 3600 * 24) {

            val amount = BigDecimal.fromDouble(round(random.nextDouble(0.0, 1000.0) * 100) / 100)
            val transactionDate = Instant.fromEpochSeconds(timeStamp)
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
                    transactionDate = transactionDate,
                    amount = amount,
                    category = category,
                    ledgerId = ledger.id,
                    currency = ledger.currency,
                    transactionType = transactionType

                )
            )


            //随机创建交易
            repeat(count) {
                val amount =
                    BigDecimal.fromDouble(round(random.nextDouble(0.0, 1000.0) * 100) / 100)
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
}