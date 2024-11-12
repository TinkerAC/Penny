package app.penny.data.repository.impl

import app.penny.data.datasource.local.UserDataManager
import app.penny.data.repository.UserDataRepository

class UserDataRepositoryImpl(
    private val userDataManager: UserDataManager
) : UserDataRepository {
    override suspend fun getRecentLedgerId(): Long {
        return userDataManager.getLong(UserDataManager.RECENT_LEDGER_ID)
    }

    override suspend fun saveRecentLedgerId(ledgerId: Long) {
        userDataManager.putLong(UserDataManager.RECENT_LEDGER_ID, ledgerId)
    }
}