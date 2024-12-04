package app.penny.routes

import app.penny.servershared.dto.requestDto.GetActionDetailRequest
import app.penny.servershared.dto.requestDto.GetActionRequest
import app.penny.servershared.dto.responseDto.GetActionResponse
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
            post("/get-action") {
                val request = call.receive<GetActionRequest>()
                val action: String? = aiService.getAction(request.text)
                if (action == null) {
                    call.respond(
                        HttpStatusCode.OK,
                        GetActionResponse(
                            success = false,
                            message = "Failed to infer action",
                            action = ""
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        GetActionResponse(
                            success = true,
                            message = "Action inferred successfully",
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