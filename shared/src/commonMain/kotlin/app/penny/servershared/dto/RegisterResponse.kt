package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    override val success: Boolean,
    override val message: String
) : BaseResponseDto()