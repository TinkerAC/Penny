package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class GetActionDetailRequest(
    val action: String
) : BaseRequestDto()