package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import kotlinx.serialization.Serializable

@Serializable
class GetAiReplyAudioRequest(
    override val invokeInstant: Long,
    override val userTimeZoneId: String,
    val fileName: String,
    val language: String
) : BaseRequestDto()