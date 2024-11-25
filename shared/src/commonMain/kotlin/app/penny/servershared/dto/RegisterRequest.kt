package app.penny.servershared.dto

import kotlinx.serialization.Serializable


@Serializable
data class RegisterRequest (
    val email: String,
    val password: String
):BaseRequestDto()