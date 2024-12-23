package app.penny.core.data.repository.impl

import app.penny.core.data.database.LedgerLocalDataSource
import app.penny.core.data.database.TransactionLocalDataSource
import app.penny.core.data.model.toModel
import app.penny.core.data.repository.StatisticRepository
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.Summary
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlin.uuid.ExperimentalUuidApi

class StatisticRepositoryImpl(
    private val transactionLocalDataSource: TransactionLocalDataSource,
    private val ledgerLocalDataSource: LedgerLocalDataSource
) : StatisticRepository {
    override suspend fun getTotalIncomeOfUser(userUuid: String): Double {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalExpenseOfUser(userUuid: String): Double {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getTotalTransactionCountByUser(user: UserModel): Long {
        return transactionLocalDataSource.findByUserUuid(user.uuid.toString()).size.toLong()
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getTransactionDateSpanDays(user: UserModel): Long {

        val latest = transactionLocalDataSource.findLatestByUserUuid(user.uuid.toString())
        val earliest = transactionLocalDataSource.findEarliestByUserUuid(user.uuid.toString())
        if (latest == null || earliest == null) {
            return 0
        } else if (latest.uuid == earliest.uuid) {
            return 1

        } else {
            val latestModel = latest.toModel()
            val earliestModel = earliest.toModel()
            return earliestModel.transactionInstant.daysUntil(
                latestModel.transactionInstant,
                timeZone = TimeZone.currentSystemDefault()
            ).toLong() + 1
        }
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
                income = BigDecimal.ZERO,
                expense = BigDecimal.ZERO,
                balance = BigDecimal.ZERO
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
            income = totalIncome,
            expense = totalExpense,
            balance = totalBalance
        )
    }


    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getUserTotalSummary(user: UserModel): Summary {
        val userLedgers =
            ledgerLocalDataSource.findByUserUuid(user.uuid.toString()).map { it.toModel() }
        return getSummary(userLedgers, null, null)

    }
}
