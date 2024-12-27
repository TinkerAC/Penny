package app.penny.servershared.dto.requestDto

import app.penny.servershared.dto.BaseRequestDto
import app.penny.servershared.dto.MonthlyReportData
import kotlinx.serialization.Serializable

@Serializable
data class GenerateMonthlyReportRequest(
    val reportData: MonthlyReportData,
    override val invokeInstant: Long,
    override val userTimeZoneId: String
) : BaseRequestDto()