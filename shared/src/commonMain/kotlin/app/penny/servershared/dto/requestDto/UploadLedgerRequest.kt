package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import app.penny.servershared.dto.LedgerDto
import kotlinx.serialization.Serializable

@Serializable
data class UploadLedgerRequest(
    val total: Int,
    val ledgers: List<LedgerDto>,
    val lastSyncedAt: Long
): BaseRequestDto()