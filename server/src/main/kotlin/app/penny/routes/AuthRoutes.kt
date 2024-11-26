package app.penny.routes

import app.penny.config.JwtConfig
import app.penny.servershared.dto.RefreshTokenRequest
import app.penny.servershared.dto.RefreshTokenResponse
import app.penny.services.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlin.math.round

fun Route.authRoutes(
    authService: AuthService, jwtConfig: JwtConfig
) {
    route("/auth") {
        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            val token = request.refreshToken
            val isRefreshTokenValid = authService.verifyRefreshToken(token)



            val userId = authService.getAuthedUserId(token)

            if (isRefreshTokenValid) {
                val accessToken = jwtConfig.makeAccessToken(userId)
                val refreshToken = jwtConfig.makeRefreshToken(userId)
                call.respond(
                    HttpStatusCode.OK, RefreshTokenResponse(
                        success = true,
                        message = "Token refreshed",
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    RefreshTokenResponse(
                        success = false,
                        message = "Invalid token"
                    )
                )
            }
            return@post
        }
    }

}