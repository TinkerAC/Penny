package app.penny.core.domain.usecase

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.enumerate.Category
import app.penny.core.domain.enumerate.TransactionType
import app.penny.core.domain.model.TransactionModel
import co.touchlab.kermit.Logger
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.round
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class InsertRandomTransactionUseCase(
    private val ledgerRepository: LedgerRepository,
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(
        count: Int,
        recentDays: Int
    ) {
        val ledgers = ledgerRepository.findAll()
        val random = Random.Default
        val transactionTypes = TransactionType.entries.toTypedArray()
        val currentTime = Clock.System.now().epochSeconds
        val rangeEnd = currentTime

        // 根据层级参数设置时间范围
        val rangeStart = rangeEnd - (recentDays * 24 * 60 * 60)

        repeat(count) {
            val amount = BigDecimal.fromDouble(round(random.nextDouble(0.0, 1000.0) * 100) / 100)
            val transactionDate = Instant.fromEpochSeconds(
                random.nextLong(rangeStart, rangeEnd)
            )
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

            transactionRepository.insert(
                TransactionModel(
                    uuid = Uuid.random(), // 随机化 UUID
                    ledgerUuid = ledger.uuid,
                    transactionInstant = transactionDate,
                    category = category,
                    transactionType = transactionType,
                    amount = amount,
                    currency = ledger.currency
                )
            )
        }


        Logger.d("Inserted $count random transactions at tier $recentDays")
    }

}