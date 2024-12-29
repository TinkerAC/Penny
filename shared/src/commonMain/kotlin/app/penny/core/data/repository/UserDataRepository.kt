package app.penny.core.data.repository

import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.UserModel
import kotlinx.datetime.Instant


interface UserDataRepository {

    suspend fun setDefaultLedger(ledger: LedgerModel)
    suspend fun getDefaultLedger(): LedgerModel
    suspend fun removeDefaultLedger()

    suspend fun setContinuousCheckInDays(days: Int)
    suspend fun getContinuousCheckInDays(): Int

    suspend fun getUser(): UserModel
    suspend fun setUser(user: UserModel)

    suspend fun getLastSyncedAt(): Instant?
    suspend fun setLastSyncedAt(lastSyncedAt: Instant)
    suspend fun removeLastSyncedAt()

    suspend fun clearUserData()


    suspend fun getUserNameOrNull(): String?
    suspend fun setUserName(userName: String)


    suspend fun getUserEmailOrNull(): String?
    suspend fun setUserEmail(userEmail: String)


    suspend fun getIsFirstTime(): Boolean
    suspend fun setIsFirstTime(isFirstTime: Boolean)


}