package app.penny.core.data.repository.impl

import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.kvstore.UserDataManager
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserDataRepositoryImpl(
    private val userDataManager: UserDataManager
) : UserDataRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getRecentLedgerUuidOrNull(): Uuid? {
        return userDataManager.getStringOrNull(UserDataManager.RECENT_LEDGER_UUID)?.let {
            Uuid.parse(it)
        }

    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun setRecentLedgerUuid(ledgerUuid: Uuid) {
        userDataManager.putString(UserDataManager.RECENT_LEDGER_UUID, ledgerUuid.toString())
    }


    override suspend fun setContinuousCheckInDays(days: Int) {
        userDataManager.putInt(UserDataManager.CONTINUOUS_CHECK_IN_DAYS, days)
    }

    override suspend fun getContinuousCheckInDays(): Int {
        return userDataManager.getInt(UserDataManager.CONTINUOUS_CHECK_IN_DAYS)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getUserUuid(): Uuid {
        val userUuid = userDataManager.getStringOrNull(UserDataManager.USER_UUID)
        return userUuid?.let { Uuid.parse(it) } ?: error("User UUID is null")
    }

    override suspend fun setUserUuid(uuid: String) {
        userDataManager.putString(UserDataManager.USER_UUID, uuid)
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
        userDataManager.putString(UserDataManager.USER_NAME, userName)
    }

    override suspend fun getUserEmailOrNull(): String? {
        return userDataManager.getStringOrNull(UserDataManager.USER_EMAIL)
    }

    override suspend fun setUserEmail(userEmail: String) {
        userDataManager.putString(UserDataManager.USER_EMAIL, userEmail)
    }


    override suspend fun clearUserData() {
        userDataManager.clear()
    }


    override suspend fun getIsFirstTime(): Boolean {
        return userDataManager.getBooleanOrNull(UserDataManager.IS_FIRST_TIME) ?: true
    }

    override suspend fun setIsFirstTime(isFirstTime: Boolean) {
        userDataManager.putBoolean(UserDataManager.IS_FIRST_TIME, isFirstTime)
    }
}