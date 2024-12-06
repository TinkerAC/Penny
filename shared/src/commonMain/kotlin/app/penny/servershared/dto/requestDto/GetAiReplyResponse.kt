package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseResponseDto
import app.penny.servershared.enumerate.Action
import kotlinx.serialization.Serializable

@Serializable
data class GetAiReplyResponse(
    override val message: String,
    override val success: Boolean,
    val content : String = "",
    val action:Action? = null
): BaseResponseDto()