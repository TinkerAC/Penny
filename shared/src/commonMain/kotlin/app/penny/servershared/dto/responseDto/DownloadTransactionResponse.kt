package app.penny.servershared.dto.responseDto

import app.penny.servershared.dto.BaseResponseDto
import app.penny.servershared.dto.TransactionDto
import kotlinx.serialization.Serializable

@Serializable
data class DownloadTransactionResponse(
    override val success: Boolean,
    override val message: String,
    val total: Int,
    val lastSyncedAt: Long,
    val transactions: List<TransactionDto>
) : BaseResponseDto()