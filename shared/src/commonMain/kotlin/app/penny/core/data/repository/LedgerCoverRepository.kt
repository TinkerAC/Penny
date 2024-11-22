package app.penny.core.data.repository

interface LedgerCoverRepository {
    suspend fun getLedgerCover(): String
    suspend fun setLedgerCover(coverUri: String)

}