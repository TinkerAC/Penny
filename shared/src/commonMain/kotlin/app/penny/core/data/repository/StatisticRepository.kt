package app.penny.core.data.repository

import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.Summary
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant

interface StatisticRepository {

    suspend fun getTotalIncomeOfUser(userUuid: String): Double
    suspend fun getTotalExpenseOfUser(userUuid: String): Double


    suspend fun getTotalTransactionCount(): Int

    suspend fun getTransactionDateSpan(): Pair<String, String>

    suspend fun getSummary(
        ledgers: List<LedgerModel>?,
        startInstant: Instant? = null,
        endInstant: Instant? = null
    ): Summary


}


