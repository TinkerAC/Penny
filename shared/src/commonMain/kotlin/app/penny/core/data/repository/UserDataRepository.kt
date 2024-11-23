package app.penny.core.data.repository

import kotlinx.datetime.Instant

interface UserDataRepository {
    suspend fun saveRecentLedgerId(ledgerId: Long?)
    suspend fun getRecentLedgerId(): Long

    suspend fun saveContinuousCheckInDays(days: Int)
    suspend fun getContinuousCheckInDays(): Int

    suspend fun getUserUuid(): String
    suspend fun saveUserUuid(uuid: String)

    suspend fun getLastSyncedAt(): Instant
    suspend fun saveLastSyncedAt(lastSyncedAt: Long)

}