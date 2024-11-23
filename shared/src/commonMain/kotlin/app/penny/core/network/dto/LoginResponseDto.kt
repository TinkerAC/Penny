package app.penny.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto
    (
    val id: Long,
    val username: String,
    val token: String
)