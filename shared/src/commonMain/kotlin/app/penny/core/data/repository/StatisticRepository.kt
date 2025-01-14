package app.penny.core.data.repository

import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.Summary
import app.penny.core.domain.model.UserModel
import kotlinx.datetime.Instant

interface StatisticRepository {

    suspend fun getTotalIncomeOfUser(userUuid: String): Double
    suspend fun getTotalExpenseOfUser(userUuid: String): Double


    suspend fun getTotalTransactionCountByUser(user:UserModel): Long

    suspend fun getTransactionDateSpanDays(user: UserModel): Long

    suspend fun getSummary(
        ledgers: List<LedgerModel>?,
        startInstant: Instant? = null,
        endInstant: Instant? = null
    ): Summary

    suspend fun getUserTotalSummary(user: UserModel): Summary



}


