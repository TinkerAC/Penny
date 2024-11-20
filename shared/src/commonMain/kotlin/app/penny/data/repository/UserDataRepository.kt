package app.penny.data.repository

interface UserDataRepository {
    suspend fun saveRecentLedgerId(ledgerId: Long?)
    suspend fun getRecentLedgerId(): Long

    suspend fun saveContinuousCheckInDays(days: Int)
    suspend fun getContinuousCheckInDays(): Int

    suspend fun getUserUuid(): String
    suspend fun saveUserUuid(uuid: String)

}