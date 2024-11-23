package app.penny.routes

import app.penny.servershared.dto.LoginRequest
import app.penny.services.UserService

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.server.routing.*
import app.penny.servershared.dto.LoginResponse
import app.penny.servershared.dto.RegisterRequest
import app.penny.servershared.dto.RegisterResponse

fun Route.userRoutes(userService: UserService) {
    route("/user") {
        post("/register") {
            val credentials = call.receive<RegisterRequest>()
            val response = userService.register(credentials)

            if (response != null) {
                call.respond(
                    RegisterResponse(
                        success = true,
                        message = "register success"
                    )
                )
            } else {
                call.respond(
                    RegisterResponse(
                        success = false,
                        message = "username is duplicated"
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