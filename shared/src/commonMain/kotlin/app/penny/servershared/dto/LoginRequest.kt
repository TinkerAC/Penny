package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val username: String? = null,
    val password: String
)