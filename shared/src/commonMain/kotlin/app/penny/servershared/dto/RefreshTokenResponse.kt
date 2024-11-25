package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    val suppress: Boolean,
    val accessToken: String? = null,
    val refreshToken: String? = null
)