package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import app.penny.servershared.dto.LedgerDto
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
data class UploadLedgerRequest(
    override var invokeInstant: Long = Clock.System.now().epochSeconds,
    override val userTimeZoneId: String =TimeZone.currentSystemDefault().id,
    val total: Int,
    val ledgers: List<LedgerDto>,
    val lastSyncedAt: Long
): BaseRequestDto()