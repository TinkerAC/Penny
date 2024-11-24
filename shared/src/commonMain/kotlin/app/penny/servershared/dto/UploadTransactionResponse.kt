package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class UploadTransactionResponse(
    val success: Boolean,
    val changedLines: Int,
    val lastSyncedAt: Long
)