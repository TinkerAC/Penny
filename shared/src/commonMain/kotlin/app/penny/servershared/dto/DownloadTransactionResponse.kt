package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class DownloadTransactionResponse(
    override val success: Boolean,
    override val message: String,
    val lastSyncedAt: Long,
    val transactions: List<TransactionDto>
) : BaseResponseDto()