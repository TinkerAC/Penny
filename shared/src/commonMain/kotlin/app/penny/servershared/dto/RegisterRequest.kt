package app.penny.servershared.dto

import kotlinx.serialization.Serializable


@Serializable
data class RegisterRequest (
    val username: String,
    val password: String
)