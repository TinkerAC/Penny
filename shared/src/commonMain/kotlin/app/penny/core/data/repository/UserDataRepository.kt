package app.penny.core.data.repository

import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
interface UserDataRepository {

    suspend fun setRecentLedgerUuid(ledgerUuid: Uuid)
    suspend fun getRecentLedgerUuidOrNull(): Uuid?

    suspend fun setContinuousCheckInDays(days: Int)
    suspend fun getContinuousCheckInDays(): Int

    suspend fun getUserUuid(): Uuid
    suspend fun setUserUuid(uuid: String)

    suspend fun getLastSyncedAt():Instant?
    suspend fun setLastSyncedAt(lastSyncedAt: Instant)

    suspend fun clearUserData()


    suspend fun getUserNameOrNull(): String?
    suspend fun setUserName(userName: String)


    suspend fun getUserEmailOrNull(): String?
    suspend fun setUserEmail(userEmail: String)



    suspend fun getIsFirstTime(): Boolean
    suspend fun setIsFirstTime(isFirstTime: Boolean)



}