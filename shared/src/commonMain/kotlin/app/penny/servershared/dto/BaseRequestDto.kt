package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
abstract class BaseRequestDto(
) {
    abstract val invokeInstant: Long
    abstract val userTimeZoneId: String
}
