package app.penny.core.network.clients

import app.penny.config.Config.API_URL
import app.penny.core.data.kvstore.TokenProvider
import app.penny.core.network.BaseAuthedApiClient
import app.penny.platform.fileSystem
import app.penny.servershared.dto.MonthlyReportData
import app.penny.servershared.dto.requestDto.GenerateMonthlyReportRequest
import app.penny.servershared.dto.requestDto.GetAiReplyRequest
import app.penny.servershared.dto.requestDto.GetAiReplyResponse
import app.penny.servershared.dto.responseDto.GenerateMonthlyReportResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.utils.io.InternalAPI
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import okio.Path.Companion.toPath

class AiApiClient(
    httpClient: HttpClient, tokenProvider: TokenProvider
) : BaseAuthedApiClient(httpClient, tokenProvider) {

    suspend fun getAiReply(
        text: String
    ): GetAiReplyResponse {
        return makeAuthenticatedRequest(
            url = "$API_URL/ai/get-reply", method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)

            setBody(
                GetAiReplyRequest(
                    text = text
                )
            )
        }


    }

    suspend fun getMonthlyReport(
        reportData: MonthlyReportData
    ): GenerateMonthlyReportResponse {
        return makeAuthenticatedRequest(
            url = "$API_URL/ai/generate-report", method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)

            setBody(
                GenerateMonthlyReportRequest(
                    reportData = reportData,
                    invokeInstant = Clock.System.now().epochSeconds,
                    userTimeZoneId = TimeZone.currentSystemDefault().id
                )
            )
        }
    }


    @OptIn(InternalAPI::class)
    suspend fun getAiReplyAudio(
        audioFilePath: String,
    ): GetAiReplyResponse {

        val path = audioFilePath.toPath()
        val audioByteArray = fileSystem.read(path) {
            readByteArray()
        }

        return makeRequestWithBinaryData<GetAiReplyResponse>(
            url = "$API_URL/ai/get-reply-audio",
            formData = formData {
                append("application/json", GetAiReplyRequest())

                //add length of byte array in the header
                append(
                    key = "application/octet-stream",
                    value = audioByteArray,
                    headers = Headers.build {
                        append("Content-Length", audioByteArray.size.toString())
                    })
            }
        )
    }
}