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
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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
    suspend fun getAiReplyAudio(audioFilePath: String): GetAiReplyResponse {

        val audioBytes = fileSystem.read(audioFilePath.toPath()) {
            readByteArray()
        }
        val response = httpClient.submitFormWithBinaryData(
            url = "$API_URL/ai/get-reply",
            formData = formData {
                // 1. 添加文本字段
                append("textData", "1234text")

                // 2. 添加文件字段；可以在 headers 中指定文件名、ContentType 等
                append(
                    key = "audioFile",
                    value = audioBytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "audio/mpeg")
                        // 或根据具体类型修改，比如 "audio/wav" 等
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=\"myAudioFile.mp3\""
                        )
                    }
                )
            }
        ) {
            // 此处也可以配置额外的请求参数、请求头等
            method = HttpMethod.Post
        }

        return response.body()
    }

}