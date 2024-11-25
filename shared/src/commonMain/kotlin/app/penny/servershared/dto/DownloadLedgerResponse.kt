import app.penny.servershared.dto.BaseResponseDto
import app.penny.servershared.dto.LedgerDto
import kotlinx.serialization.Serializable

@Serializable
data class DownloadLedgerResponse(
    override val success: Boolean,
    override val message: String,
    val ledgers: List<LedgerDto>
) : BaseResponseDto()