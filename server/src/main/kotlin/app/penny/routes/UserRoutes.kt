package app.penny.routes

import app.penny.config.JwtConfig
import app.penny.servershared.dto.CheckIsEmailRegisteredResponse
import app.penny.servershared.dto.LoginRequest
import app.penny.servershared.dto.LoginResponse
import app.penny.servershared.dto.RegisterRequest
import app.penny.servershared.dto.RegisterResponse
import app.penny.services.UserService
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.logging.Logger

fun Route.userRoutes(userService: UserService, jwtConfig: JwtConfig) {
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
                call.respond(
                    LoginResponse(
                        userDto = response.userDto,
                        token = response.token,
                        success = true
                    )
                )
            } else {
                call.respond(
                    LoginResponse(
                        userDto = null,
                        token = null,
                        success = false
                    )
                )
            }
        }

        get(
            "/checkIsEmailRegistered"
        ) {


            val email = call.request.queryParameters["email"] ?: ""
            if (email.isEmpty()) {
                call.respond(
                    CheckIsEmailRegisteredResponse(
                        isEmailRegistered = false
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