package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import kotlinx.serialization.Serializable


@Serializable
data class RegisterRequest(
    val uuid: String?,
    val email: String,
    val password: String
): BaseRequestDto()