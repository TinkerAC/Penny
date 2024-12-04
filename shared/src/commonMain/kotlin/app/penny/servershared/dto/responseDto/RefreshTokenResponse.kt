package app.penny.servershared.dto.responseDto

import app.penny.servershared.dto.BaseResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    override val success: Boolean,
    override val message: String,
    val accessToken: String? = null,
    val refreshToken: String? = null
) : BaseResponseDto()