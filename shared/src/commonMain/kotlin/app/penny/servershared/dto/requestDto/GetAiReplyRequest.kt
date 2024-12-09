package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class GetAiReplyRequest(
    override var invokeInstant: Long,
    override val userTimeZoneId: String,
    var text: String? = null
) : BaseRequestDto()