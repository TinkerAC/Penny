package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val username: String? = null,
    val password: String
): BaseRequestDto()