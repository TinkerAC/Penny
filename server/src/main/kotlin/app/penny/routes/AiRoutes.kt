// file: server/src/main/kotlin/app/penny/routes/AiRoutes.kt
package app.penny.routes

import app.penny.servershared.dto.requestDto.GetActionDetailRequest
import app.penny.servershared.dto.requestDto.GetActionRequest
import app.penny.servershared.dto.requestDto.GetAiReplyRequest
import app.penny.servershared.dto.requestDto.GetAiReplyResponse
import app.penny.servershared.enumerate.Action
import app.penny.services.AiService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.aiRoutes(
    aiService: AiService
) {
    route("/ai") {
        authenticate("access-jwt") {
            /**
             * Endpoint to get AI-inferred action based on user input text.
             */
            post("/get-reply") {
                val request = call.receive<GetAiReplyRequest>()

                val action: Action? = aiService.getAction(
                    call = call,
                    text = request.text?.trim() ?: ""
                )

                if (action != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        GetAiReplyResponse(
                            success = true,
                            message = "Successfully retrieved action",
                            action = action
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        GetAiReplyResponse(
                            success = false,
                            message = "Failed to retrieve action",
                            content = "Failed to retrieve action"
                        )
                    )
                }
            }


        }
    }
}
