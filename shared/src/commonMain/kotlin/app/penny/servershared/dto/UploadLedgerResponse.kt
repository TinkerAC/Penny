package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class UploadLedgerResponse(
    val success: Boolean,
    val changedLines: Int,
    val lastSyncedAt: Long
)