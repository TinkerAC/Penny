// file: server/src/main/kotlin/app/penny/routes/AiRoutes.kt
package app.penny.routes

import app.penny.servershared.dto.requestDto.GenerateMonthlyReportRequest
import app.penny.servershared.dto.requestDto.GetAiReplyRequest
import app.penny.servershared.dto.requestDto.GetAiReplyResponse
import app.penny.servershared.dto.responseDto.GenerateMonthlyReportResponse
import app.penny.servershared.enumerate.UserIntent
import app.penny.services.AiService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

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


        }
    }
}
