package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class UploadTransactionRequest(
    val transactions: List<TransactionDto>,
    val lastSynced: Long
) : BaseRequestDto()