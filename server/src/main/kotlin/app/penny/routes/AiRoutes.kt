// file: server/src/main/kotlin/app/penny/routes/AiRoutes.kt
package app.penny.routes

import app.penny.core.data.enumerate.json
import app.penny.servershared.dto.requestDto.GenerateMonthlyReportRequest
import app.penny.servershared.dto.requestDto.GetAiReplyAudioRequest
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
import kotlinx.io.files.Path
import java.io.File

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
                // 1. 接收 Multipart
                val multipart = call.receiveMultipart()

                var requestJson: GetAiReplyAudioRequest? = null
                var audioBytesReceived: ByteArray? = null

                // 2. 遍历每个 Part
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            // 如果 name == "textData"，提取出文本
                            if (part.name == "requestJson") {
                                requestJson = json.decodeFromString(
                                    GetAiReplyAudioRequest.serializer(),
                                    part.value
                                )
                            }
                        }

                        is PartData.FileItem -> {
                            // 如果 name == "audioFile"，读取出二进制流
                            if (part.name == "audioFile") {
                                audioBytesReceived = part.provider().readByteArray(
                                    count = part.headers["Content-Length"]?.toInt() ?: 0
                                )
                            }
                        }

                        else -> {}
                    }
                    // 使用完后需要 dispose
                    part.dispose()
                }

                // 3. 做一些简单的处理，比如打印或存储到数据库/文件系统
                val message = buildString {
                    appendLine("接收到的文本：${requestJson}")
                    appendLine(
                        "接收到的音频大小：${audioBytesReceived?.size ?: 0} bytes(${
                            audioBytesReceived?.size?.div(
                                1024
                            )
                        } KB)"
                    )
                }

                println(message)

                //save the audio file temporarily for debug
                val audioFilePath = "audio-debug.wav"
                val file = File(audioFilePath)

                file.writeBytes(audioBytesReceived!!)
//                throw Exception("audio file saved to $audioFilePath")
                try {
                    // 将音频转录为文本
                    val transcribedText = aiService.audioToText(
                        path = Path(audioFilePath),
                        requestDto = requestJson!!
                    )

                    // 调用原有 get-ai-reply 逻辑
                    val userIntent: UserIntent = aiService.getUserIntent(
                        call = call,
                        text = transcribedText,
                        invokeInstant = requestJson!!.invokeInstant,
                        userTimeZoneId = requestJson!!.userTimeZoneId
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
                    e.printStackTrace()
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        "Error processing audio input: ${e.message}"
                    )
                }
            }


        }
    }
}
