package app.penny.servershared.dto.responseDto

import app.penny.servershared.dto.BaseResponseDto
import app.penny.servershared.dto.LedgerDto
import kotlinx.serialization.Serializable

@Serializable
data class DownloadLedgerResponse(
    override val success: Boolean,
    override val message: String,
    val total: Int = 0,
    val ledgers: List<LedgerDto> = emptyList(),
    val lastSyncedAt: Long? = null
) : BaseResponseDto()