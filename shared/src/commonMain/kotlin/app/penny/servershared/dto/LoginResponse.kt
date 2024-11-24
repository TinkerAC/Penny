package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String?= null,
    val userDto: UserDto?,
    val token: String?
)