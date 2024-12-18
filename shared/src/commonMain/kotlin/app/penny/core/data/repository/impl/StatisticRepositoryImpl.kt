package app.penny.core.data.repository.impl

import app.penny.core.data.repository.StatisticRepository

class StatisticRepositoryImpl:StatisticRepository {
    override suspend fun getTotalIncomeOfLedger(ledgerUuid: String): Double {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalExpenseOfLedger(ledgerUuid: String): Double {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalBalanceOfLedger(ledgerUuid: String): Double {
        TODO("Not yet implemented")
    }

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
}