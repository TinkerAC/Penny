package app.penny.data.repository

interface LedgerCoverRepository {
    suspend fun getLedgerCover(): String
    suspend fun setLedgerCover(coverUri: String)

}