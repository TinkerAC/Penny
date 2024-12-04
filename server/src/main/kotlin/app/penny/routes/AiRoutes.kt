package app.penny.routes

import app.penny.servershared.dto.requestDto.GetActionDetailRequest
import app.penny.servershared.dto.requestDto.GetActionRequest
import app.penny.servershared.dto.requestDto.GetAiReplyResponse
import app.penny.servershared.enumerate.Action
import app.penny.services.AiService
import io.ktor.server.routing.Route

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.aiRoutes(
    aiService: AiService
) {

    route("/ai") {
        authenticate(
            "access-jwt"
        ) {
            post("/get-reply") {
                val request = call.receive<GetActionRequest>()
                val action: Action? = aiService.getAction(request.text)

                if (action == null) {
                    call.respond(
                        HttpStatusCode.OK,
                        GetAiReplyResponse(
                            success = false,
                            message = "Failed to infer action",
                            content = "Failed to infer action"
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        GetAiReplyResponse(
                            success = true,
                            message = "Successfully inferred action",
                            action = action
                        )
                    )
                }
            }

            post("/get-action-detail") {
                val request: GetActionDetailRequest = call.receive<GetActionDetailRequest>()
            }

        }

    }
}