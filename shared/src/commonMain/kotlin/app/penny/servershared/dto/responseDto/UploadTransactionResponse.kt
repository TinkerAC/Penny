package app.penny.servershared.dto.responseDto

import app.penny.servershared.dto.BaseResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class UploadTransactionResponse(
    override val message: String,
    override val success: Boolean,
    val changedLines: Int,
    val lastSyncedAt: Long
) : BaseResponseDto()