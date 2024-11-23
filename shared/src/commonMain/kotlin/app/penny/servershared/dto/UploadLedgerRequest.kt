package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class UploadLedgerRequest(
    val total: Int,
    val ledgers: List<LedgerDto>,
    val lastSyncedAt: Long
)