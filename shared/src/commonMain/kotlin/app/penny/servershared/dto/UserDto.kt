package app.penny.servershared.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserDto(
    val id: Long = 0,
    val username: String? = null,
    val email: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val passwordHash: String? = null
)
