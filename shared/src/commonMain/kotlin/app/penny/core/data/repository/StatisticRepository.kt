package app.penny.core.data.repository

interface StatisticRepository {
    suspend fun getTotalIncomeOfLedger(ledgerUuid: String): Double
    suspend fun getTotalExpenseOfLedger(ledgerUuid: String): Double
    suspend fun getTotalBalanceOfLedger(ledgerUuid: String): Double

    suspend fun getTotalIncomeOfUser(userUuid: String): Double
    suspend fun getTotalExpenseOfUser(userUuid: String): Double


    suspend fun getTotalTransactionCount(): Int

    suspend fun getTransactionDateSpan(): Pair<String, String>



}