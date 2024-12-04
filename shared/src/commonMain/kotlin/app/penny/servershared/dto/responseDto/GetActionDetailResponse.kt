package app.penny.servershared.dto.responseDto

import app.penny.servershared.dto.ActionDetailDto
import app.penny.servershared.dto.BaseResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class GetActionDetailResponse(
    override val message: String,
    override val success: Boolean,
    val actionDetail: ActionDetailDto
) : BaseResponseDto()