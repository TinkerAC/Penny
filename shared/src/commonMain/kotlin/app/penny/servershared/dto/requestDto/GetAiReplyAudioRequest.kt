package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto

class GetAiReplyAudioRequest(
    override val invokeInstant: Long,
    override val userTimeZoneId: String,
    var text: String? = null
) : BaseRequestDto()