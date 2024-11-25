package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class UploadTransactionsRequest(
    val transactions: List<TransactionDto>,
    val lastSynced: Long
) : BaseRequestDto()