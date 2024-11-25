package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class UploadLedgerResponse(
    override val success: Boolean,
    override val message: String,
    val changedLines: Int,
    val lastSyncedAt: Long,
) : BaseResponseDto()