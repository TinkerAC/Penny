package app.penny.core.data.repository

import kotlinx.datetime.Instant

interface UserDataRepository {
    suspend fun setRecentLedgerId(ledgerId: Long?)
    suspend fun getRecentLedgerId(): Long

    suspend fun setContinuousCheckInDays(days: Int)
    suspend fun getContinuousCheckInDays(): Int

    suspend fun getUserUuid(): String
    suspend fun setUserUuid(uuid: String)

    suspend fun getLastSyncedAt(): Instant
    suspend fun setLastSyncedAt(lastSyncedAt: Instant)

}