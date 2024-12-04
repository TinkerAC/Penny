package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class CheckIsEmailRegisteredRequest(
    val email: String
) : BaseRequestDto()