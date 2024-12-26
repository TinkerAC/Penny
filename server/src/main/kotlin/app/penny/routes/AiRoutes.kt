// file: server/src/main/kotlin/app/penny/routes/AiRoutes.kt
package app.penny.routes

import app.penny.servershared.dto.requestDto.GetAiReplyRequest
import app.penny.servershared.dto.requestDto.GetAiReplyResponse
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

                val userIntent: UserIntent? = aiService.getUserIntent(
                    call = call,
                    text = request.text?.trim() ?: "",
                    invokeInstant = request.invokeInstant,
                    userTimeZoneId = request.userTimeZoneId
                )

                if (userIntent != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        GetAiReplyResponse(
                            success = true,
                            message = "Successfully retrieved userIntent",
                            userIntent = userIntent
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        GetAiReplyResponse(
                            success = false,
                            message = "Failed to retrieve userIntent",
                            content = "Failed to retrieve userIntent"
                        )
                    )
                }
            }


        }
    }
}
