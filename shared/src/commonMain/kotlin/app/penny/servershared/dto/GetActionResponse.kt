package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetActionResponse(
    override val success: Boolean,
    override val message: String,
    val action: String
) : BaseResponseDto()