package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
abstract class BaseResponseDto(
) {
    abstract val success: Boolean
    abstract val message: String
}