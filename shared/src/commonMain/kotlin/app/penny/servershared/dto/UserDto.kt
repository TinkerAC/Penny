package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long = 0,
    val username: String? = null,
    val email: String = "",
    val createdAt: Long = 0,
)