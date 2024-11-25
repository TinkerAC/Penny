package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    override val success: Boolean,
    override val message: String,
    val userDto: UserDto? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
):BaseResponseDto()