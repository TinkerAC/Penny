package app.penny.core.data.repository.impl

import app.penny.core.data.database.TransactionLocalDataSource
import app.penny.core.data.model.toModel
import app.penny.core.data.repository.StatisticRepository
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.Summary
import app.penny.core.domain.model.TransactionModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi

class StatisticRepositoryImpl(
    private val transactionLocalDataSource: TransactionLocalDataSource
) : StatisticRepository {
    override suspend fun getTotalIncomeOfUser(userUuid: String): Double {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalExpenseOfUser(userUuid: String): Double {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalTransactionCount(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getTransactionDateSpan(): Pair<String, String> {
        TODO("Not yet implemented")
    }


    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getSummary(
        ledgers: List<LedgerModel>?,
        startInstant: Instant?,
        endInstant: Instant?
    ): Summary {
        // 如果没有提供任何账本，返回零汇总
        if (ledgers.isNullOrEmpty()) {
            return Summary(
                totalIncome = BigDecimal.ZERO,
                totalExpense = BigDecimal.ZERO,
                totalBalance = BigDecimal.ZERO
            )
        }

        // 获取所有相关账本的UUID列表
        val ledgerUuids = ledgers.map { it.uuid.toString() }
        // 根据UUID列表和可选的时间范围查询交易
        val allTransactions: List<TransactionModel> =
            transactionLocalDataSource.findByLedgerUuids(ledgerUuids).map { it.toModel() }
                .filter { transaction ->
                    val transactionInstant = transaction.transactionInstant
                    val afterStart = startInstant?.let { transactionInstant >= it } ?: true
                    val beforeEnd = endInstant?.let { transactionInstant <= it } ?: true
                    afterStart && beforeEnd
                }

        // 计算总收入
        val totalIncome: BigDecimal = allTransactions
            .asSequence()
            .filter { it.transactionType == TransactionType.INCOME }
            .map { it.amount }
            .fold(BigDecimal.ZERO) { acc, amount -> acc + amount }

        // 计算总支出
        val totalExpense: BigDecimal = allTransactions
            .asSequence()
            .filter { it.transactionType == TransactionType.EXPENSE }
            .map { it.amount }
            .fold(BigDecimal.ZERO) { acc, amount -> acc + amount }

        // 计算总余额
        val totalBalance = totalIncome - totalExpense

        return Summary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            totalBalance = totalBalance
        )
    }

}
