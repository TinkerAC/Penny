// 文件：server/src/main/kotlin/app/penny/routes/AuthRoutes.kt
package app.penny.routes

import app.penny.servershared.dto.RefreshTokenRequest
import app.penny.services.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            val token = request.refreshToken

            val refreshResult = authService.refreshTokens(token)
            if (refreshResult.success) {
                call.respond(HttpStatusCode.OK, refreshResult)
            } else {
                call.respond(HttpStatusCode.Unauthorized, refreshResult)
            }
        }
    }
}