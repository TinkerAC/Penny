package app.penny.servershared.dto.responseDto

import app.penny.servershared.dto.BaseResponseDto
import app.penny.servershared.dto.entityDto.UserDto
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    override val success: Boolean,
    override val message: String,

    val userDto: UserDto?

) : BaseResponseDto()