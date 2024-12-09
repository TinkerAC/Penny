package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class GetActionDetailRequest(
    override var invokeInstant: Long,
    override val userTimeZoneId: String,
    val action: String
) : BaseRequestDto()