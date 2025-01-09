package app.penny.core.data.repository.impl

import app.penny.core.data.database.LedgerLocalDataSource
import app.penny.core.data.database.UserLocalDataSource
import app.penny.core.data.kvstore.UserDataManager
import app.penny.core.data.enumerate.toModel
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.UserModel
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi

class UserDataRepositoryImpl(
    private val userDataManager: UserDataManager,
    private val ledgerLocalDataSource: LedgerLocalDataSource,
    private val userLocalDataRepository: UserLocalDataSource,
) : UserDataRepository {
    override suspend fun getDefaultLedger(): LedgerModel {
        val defaultLedgerUuid = userDataManager.getStringOrNull(UserDataManager.DEFAULT_LEDGER_UUID)
        return ledgerLocalDataSource.findByUuid(
            uuid = defaultLedgerUuid!!
        )!!.toModel()
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun setDefaultLedger(ledger: LedgerModel) {
        userDataManager.putString(UserDataManager.DEFAULT_LEDGER_UUID, ledger.uuid.toString())
    }

    override suspend fun removeDefaultLedger() {
        userDataManager.remove(UserDataManager.DEFAULT_LEDGER_UUID)
    }

    override suspend fun setContinuousCheckInDays(days: Int) {
        userDataManager.putInt(UserDataManager.CONTINUOUS_CHECK_IN_DAYS, days)
    }

    override suspend fun getContinuousCheckInDays(): Int {
        return userDataManager.getInt(UserDataManager.CONTINUOUS_CHECK_IN_DAYS)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getUser(): UserModel {
        val userUuid = userDataManager.getStringOrNull(UserDataManager.USER_UUID)!!
        return userLocalDataRepository.findByUuid(
            uuid = userUuid
        )!!.toModel()
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun setUser(user: UserModel) {
        userDataManager.putString(UserDataManager.USER_UUID, user.uuid.toString())
    }

    override suspend fun getLastSyncedAt(): Instant? {
        val lastSyncedAt = userDataManager.getLongOrNull(UserDataManager.LAST_SYNCED_AT)
        return lastSyncedAt?.let { Instant.fromEpochSeconds(it) }
    }


    override suspend fun setLastSyncedAt(lastSyncedAt: Instant) {
        userDataManager.putLong(UserDataManager.LAST_SYNCED_AT, lastSyncedAt.epochSeconds)
    }

    override suspend fun removeLastSyncedAt() {
        userDataManager.remove(UserDataManager.LAST_SYNCED_AT)
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