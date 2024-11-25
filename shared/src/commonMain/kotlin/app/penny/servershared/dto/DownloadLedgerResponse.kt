package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class DownloadLedgerResponse(
    val message: String = "",
    val total: Int,
    val ledgers: List<LedgerDto>,
    val lastSyncedAt: Long

)
