package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class UploadTransactionResponse(
    override val message: String,
    override val success: Boolean,
) : BaseResponseDto()