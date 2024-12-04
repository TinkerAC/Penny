package app.penny.servershared.dto.responseDto

import app.penny.servershared.dto.BaseResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class CheckIsEmailRegisteredResponse(

    override val success: Boolean,
    override val message: String,
    val isEmailRegistered: Boolean? = null

    ) : BaseResponseDto()