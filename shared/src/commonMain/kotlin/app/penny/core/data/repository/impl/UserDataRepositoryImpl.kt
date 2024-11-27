package app.penny.core.data.repository.impl

import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.kvstore.UserDataManager
import co.touchlab.kermit.Logger
import kotlinx.datetime.Instant

class UserDataRepositoryImpl(
    private val userDataManager: UserDataManager
) : UserDataRepository {
    override suspend fun getRecentLedgerIdOrNull(): Long? {

        return userDataManager.getLongOrNull(UserDataManager.RECENT_LEDGER_ID)
    }

    override suspend fun setRecentLedgerId(ledgerId: Long) {
        userDataManager.putLong(UserDataManager.RECENT_LEDGER_ID, ledgerId)
    }


    override suspend fun setContinuousCheckInDays(days: Int) {
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

    override suspend fun setUserUuid(uuid: String) {
        userDataManager.setString(UserDataManager.USER_UUID, uuid)
    }

    override suspend fun getLastSyncedAt(): Instant? {
        val lastSyncedAt = userDataManager.getLongOrNull(UserDataManager.LAST_SYNCED_AT)
        return lastSyncedAt?.let { Instant.fromEpochSeconds(it) }
    }


    override suspend fun setLastSyncedAt(lastSyncedAt: Instant) {
        userDataManager.putLong(UserDataManager.LAST_SYNCED_AT, lastSyncedAt.epochSeconds)
    }

    override suspend fun getUserNameOrNull(): String? {
        return userDataManager.getStringOrNull(UserDataManager.USER_NAME)
    }

    override suspend fun setUserName(userName: String) {
        userDataManager.setString(UserDataManager.USER_NAME, userName)
    }

    override suspend fun getUserEmailOrNull(): String? {
        return userDataManager.getStringOrNull(UserDataManager.USER_EMAIL)
    }

    override suspend fun setUserEmail(userEmail: String) {
        userDataManager.setString(UserDataManager.USER_EMAIL, userEmail)
    }


    override suspend fun clearUserData() {
        userDataManager.clear()
    }
}