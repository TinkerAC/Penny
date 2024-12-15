package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    override var invokeInstant: Long = Clock.System.now().epochSeconds,
    override val userTimeZoneId: String = TimeZone.currentSystemDefault().id,
    val refreshToken: String
) : BaseRequestDto()