package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable


@Serializable
data class RegisterRequest(
    override val invokeInstant: Long = Clock.System.now().epochSeconds,
    override val userTimeZoneId: String = TimeZone.currentSystemDefault().id,
    val uuid: String?,
    val email: String,
    val password: String
) : BaseRequestDto()