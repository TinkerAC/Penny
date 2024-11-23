package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val success: Boolean,
    val message: String
)