// file: server/src/main/kotlin/app/penny/routes/UserRoutes.kt
package app.penny.routes

import app.penny.config.JwtConfig
import app.penny.servershared.dto.CheckIsEmailRegisteredResponse
import app.penny.servershared.dto.LoginRequest
import app.penny.servershared.dto.LoginResponse
import app.penny.servershared.dto.RegisterRequest
import app.penny.servershared.dto.RegisterResponse
import app.penny.services.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.userRoutes(userService: UserService) {
    route("/user") {
        post("/register") {
            val credentials = call.receive<RegisterRequest>()
            val success = userService.register(credentials)
            if (success) {
                call.respond(
                    RegisterResponse(
                        success = true,
                        message = "User registered successfully"
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.Conflict,
                    RegisterResponse(
                        success = false,
                        message = "User already exists"
                    )
                )
            }
        }

        post("/login") {
            val credentials = call.receive<LoginRequest>()
            val response = userService.login(credentials)
            if (response.success) {
                call.respond(response)
            } else {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    response
                )
            }
        }

        get("/checkIsEmailRegistered") {
            val email = call.request.queryParameters["email"]
            if (email.isNullOrEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    CheckIsEmailRegisteredResponse(
                        isEmailRegistered = false,
                        message = "Email parameter is missing"
                    )
                )
                return@get
            }

            val isEmailRegistered = userService.checkIsEmailRegistered(email)
            call.respond(
                CheckIsEmailRegisteredResponse(
                    isEmailRegistered = isEmailRegistered
                )
            )
        }
    }
}
