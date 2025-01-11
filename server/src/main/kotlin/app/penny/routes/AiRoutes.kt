// file: server/src/main/kotlin/app/penny/routes/AiRoutes.kt
package app.penny.routes

import app.penny.core.data.enumerate.json
import app.penny.servershared.dto.requestDto.GenerateMonthlyReportRequest
import app.penny.servershared.dto.requestDto.GetAiReplyRequest
import app.penny.servershared.dto.requestDto.GetAiReplyResponse
import app.penny.servershared.dto.responseDto.GenerateMonthlyReportResponse
import app.penny.servershared.enumerate.UserIntent
import app.penny.services.AiService
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.readByteArray

fun Route.aiRoutes(
    aiService: AiService
) {
    route("/ai") {
        authenticate("access-jwt") {

            /**
             * Endpoint to get AI-inferred userIntent based on user input text.
             */
            post("/get-reply") {
                val request = call.receive<GetAiReplyRequest>()
                val userIntent: UserIntent = aiService.getUserIntent(
                    call = call,
                    text = request.text?.trim() ?: "",
                    invokeInstant = request.invokeInstant,
                    userTimeZoneId = request.userTimeZoneId
                )
                println("UserIntent: $userIntent")
                call.respond(
                    HttpStatusCode.OK,
                    GetAiReplyResponse(
                        success = true,
                        message = "Successfully retrieved userIntent",
                        userIntent = userIntent
                    )
                )
            }

            /**
             * Endpoint to generate a monthly report based on the userIntent.
             */
            post("/generate-report") {
                val request = call.receive<GenerateMonthlyReportRequest>()
                val report = aiService.generateReport(
                    request.reportData,
                )
                if (report != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        GenerateMonthlyReportResponse(
                            success = true,
                            message = "Successfully generated monthly report",
                            report = report
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        GenerateMonthlyReportResponse(
                            success = false,
                            message = "Failed to generate monthly report",
                            report = ""
                        )
                    )
                }
            }

            post("/get-reply-audio") {
                val multipart = call.receiveMultipart()

                var jsonRequest: GetAiReplyRequest? = null
                var audioBytes: ByteArray? = null

                // 解析 multipart 请求
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            // 检查是否为 JSON 数据
                            if (part.name == "application/json") {
                                jsonRequest = part.value.let {
                                    json.decodeFromString(GetAiReplyRequest.serializer(), it)
                                }
                            }
                        }

                        is PartData.FileItem -> {
                            if (part.name == "application/octet-stream") {
                                audioBytes = part.provider().readByteArray(
                                    count = part.headers["Content-Length"]?.toInt() ?: 0
                                )
                            }
                        }

                        else -> {}
                    }
                    part.dispose()
                }

                if (jsonRequest == null || audioBytes == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Missing required data (JSON request or audio file)"
                    )
                    return@post
                }

                try {
                    // 将音频转录为文本
                    val transcribedText = aiService.audioToText(audioBytes!!)

                    // 调用原有 get-ai-reply 逻辑
                    val userIntent: UserIntent = aiService.getUserIntent(
                        call = call,
                        text = transcribedText,
                        invokeInstant = jsonRequest!!.invokeInstant,
                        userTimeZoneId = jsonRequest!!.userTimeZoneId
                    )

                    call.respond(
                        HttpStatusCode.OK,
                        GetAiReplyResponse(
                            success = true,
                            message = "Successfully retrieved userIntent from audio",
                            userIntent = userIntent
                        )
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        "Error processing audio input: ${e.message}"
                    )
                }
            }


        }
    }
}
