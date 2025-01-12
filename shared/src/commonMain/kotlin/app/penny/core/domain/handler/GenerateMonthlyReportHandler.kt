package app.penny.core.domain.handler

import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.core.domain.usecase.PrepareMonthlyReportDataUseCase
import app.penny.core.network.clients.AiApiClient
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.dto.responseDto.GenerateMonthlyReportResponse
import app.penny.servershared.enumerate.UserIntent
import app.penny.servershared.enumerate.UserIntentStatus
import kotlin.uuid.ExperimentalUuidApi

class GenerateMonthlyReportHandler(
    private val aiApiClient: AiApiClient,
    private val prepareMonthlyReportDataUseCase: PrepareMonthlyReportDataUseCase,
    private val userDataRepository: UserDataRepository
) : UserIntentHandler {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun handle(message: SystemMessage, dto: BaseEntityDto?): SystemMessage {
        //receive yearMonth from message
        if (message.userIntent !is UserIntent.GenerateMonthlyReport) {
            throw IllegalArgumentException("Unsupported userIntent type")
        }
        //if can not parse from message, use LastMonth
        val year = message.userIntent.year
        val month = message.userIntent.month

        val yearMonth = year?.let {
            month?.let {
                YearMonth(year, month)
            }
        } ?: YearMonth.now().plusMonths(-1)


        val data = prepareMonthlyReportDataUseCase(
            ledger = userDataRepository.getDefaultLedger(), yearMonth = yearMonth
        )

        //send to api to get report text


        val response: GenerateMonthlyReportResponse = aiApiClient.getMonthlyReport(
            reportData = data
        )

        return when (response.success) {
            true -> message.copy(
                userIntent = message.userIntent.copy(
                    status = UserIntentStatus.Completed,
                ), content = response.report
            )

            false -> message.copy(
                userIntent = message.userIntent.copy(
                    status = UserIntentStatus.Failed,
                ), content = "Failed to generate monthly report"
            )
        }
    }
}