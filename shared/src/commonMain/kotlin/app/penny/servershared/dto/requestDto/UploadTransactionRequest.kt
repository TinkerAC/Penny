package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import app.penny.servershared.dto.entityDto.TransactionDto
import kotlinx.serialization.Serializable

@Serializable
data class UploadTransactionRequest(
    val transactions: List<TransactionDto>,
    val lastSynced: Long
) : BaseRequestDto()