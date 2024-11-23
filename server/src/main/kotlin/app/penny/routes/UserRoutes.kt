package app.penny.routes

import app.penny.config.JwtConfig
import app.penny.servershared.dto.LoginRequest
import app.penny.servershared.dto.LoginResponse
import app.penny.servershared.dto.RegisterRequest
import app.penny.servershared.dto.RegisterResponse
import app.penny.services.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

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
            if (response != null) {
                call.respond(
                    LoginResponse(
                        id = response.id,
                        username = response.username,
                        token = response.token
                    )
                )
            } else {
                call.respond(
                    LoginResponse(
                        id = -1,
                        username = "",
                        token = ""
                    )
                )
            }
        }
    }
}