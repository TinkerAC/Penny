package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import app.penny.servershared.dto.TransactionDto
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
data class UploadTransactionRequest(
    override var invokeInstant: Long = Clock.System.now().epochSeconds,
    override val userTimeZoneId: String = TimeZone.currentSystemDefault().id,
    val transactions: List<TransactionDto>,
    val lastSynced: Long
) : BaseRequestDto()