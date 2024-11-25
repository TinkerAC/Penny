package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    val success: Boolean,
    val accessToken: String? = null,
    val refreshToken: String? = null
)