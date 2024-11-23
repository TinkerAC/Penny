package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class DownloadLedgerRequest(
    val lastSyncedAt: Long
)