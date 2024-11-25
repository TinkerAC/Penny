package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckIsEmailRegisteredResponse(

    override val success: Boolean,
    override val message: String,
    val isEmailRegistered: Boolean? = null

    ) : BaseResponseDto()