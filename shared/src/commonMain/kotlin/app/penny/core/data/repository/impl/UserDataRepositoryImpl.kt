package app.penny.core.data.repository.impl

import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.kvstore.UserDataStorage
import co.touchlab.kermit.Logger
import kotlinx.datetime.Instant

class UserDataRepositoryImpl(
    private val userDataStorage: UserDataStorage
) : UserDataRepository {
    override suspend fun getRecentLedgerIdOrNull(): Long? {

        return userDataStorage.getLongOrNull(UserDataStorage.RECENT_LEDGER_ID)
    }

    override suspend fun setRecentLedgerId(ledgerId: Long) {
        userDataStorage.putLong(UserDataStorage.RECENT_LEDGER_ID, ledgerId)
    }


    override suspend fun setContinuousCheckInDays(days: Int) {
        userDataStorage.putInt(UserDataStorage.CONTINUOUS_CHECK_IN_DAYS, days)
    }

    override suspend fun getContinuousCheckInDays(): Int {
        return userDataStorage.getInt(UserDataStorage.CONTINUOUS_CHECK_IN_DAYS)
    }

    override suspend fun getUserUuid(): String {
        if (userDataStorage.getNonFlowString(UserDataStorage.USER_UUID).isEmpty()) {
            Logger.d("No user uuid found, generating new one")
            userDataStorage.setString(UserDataStorage.USER_UUID, userDataStorage.generateUserUuid())
        }
        return userDataStorage.getNonFlowString(UserDataStorage.USER_UUID)
    }

    override suspend fun setUserUuid(uuid: String) {
        userDataStorage.setString(UserDataStorage.USER_UUID, uuid)
    }

    override suspend fun getLastSyncedAt(): Instant? {
        val lastSyncedAt = userDataStorage.getLongOrNull(UserDataStorage.LAST_SYNCED_AT)
        return lastSyncedAt?.let { Instant.fromEpochSeconds(it) }
    }


    override suspend fun setLastSyncedAt(lastSyncedAt: Instant) {
        userDataStorage.putLong(UserDataStorage.LAST_SYNCED_AT, lastSyncedAt.epochSeconds)
    }

    override suspend fun getUserNameOrNull(): String? {
        return userDataStorage.getStringOrNull(UserDataStorage.USER_NAME)
    }

    override suspend fun setUserName(userName: String) {
        userDataStorage.setString(UserDataStorage.USER_NAME, userName)
    }

    override suspend fun getUserEmailOrNull(): String? {
        return userDataStorage.getStringOrNull(UserDataStorage.USER_EMAIL)
    }

    override suspend fun setUserEmail(userEmail: String) {
        userDataStorage.setString(UserDataStorage.USER_EMAIL, userEmail)
    }


    override suspend fun clearUserData() {

        userDataStorage.clear()
    }
}