package app.penny.core.data.repository.impl

import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.kvstore.UserDataManager
import co.touchlab.kermit.Logger

class UserDataRepositoryImpl(
    private val userDataManager: UserDataManager
) : UserDataRepository {
    override suspend fun getRecentLedgerId(): Long {
        return userDataManager.getLong(UserDataManager.RECENT_LEDGER_ID)
    }

    override suspend fun saveRecentLedgerId(ledgerId: Long?) {
        userDataManager.putLong(UserDataManager.RECENT_LEDGER_ID, ledgerId ?: 0)
    }

    override suspend fun saveContinuousCheckInDays(days: Int) {
        userDataManager.putInt(UserDataManager.CONTINUOUS_CHECK_IN_DAYS, days)
    }

    override suspend fun getContinuousCheckInDays(): Int {
        return userDataManager.getInt(UserDataManager.CONTINUOUS_CHECK_IN_DAYS)
    }

    override suspend fun getUserUuid(): String {
        if (userDataManager.getNonFlowString(UserDataManager.USER_UUID).isEmpty()) {
            Logger.d("No user uuid found, generating new one")
            userDataManager.setString(UserDataManager.USER_UUID, userDataManager.generateUserUuid())
        }
        return userDataManager.getNonFlowString(UserDataManager.USER_UUID)
    }

    override suspend fun saveUserUuid(uuid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getLastSyncedAt(): Long {
        if (userDataManager.getLong(UserDataManager.LAST_SYNCED_AT) == 0L) {
            Logger.d("No last synced at found, setting to 0")
            userDataManager.putLong(UserDataManager.LAST_SYNCED_AT, 0)
        }
        return userDataManager.getLong(UserDataManager.LAST_SYNCED_AT)
    }


    override suspend fun saveLastSyncedAt(lastSyncedAt: Long) {
        userDataManager.putLong(UserDataManager.LAST_SYNCED_AT, lastSyncedAt)
    }
}