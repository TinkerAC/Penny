package app.penny.routes

import app.penny.config.JwtConfig
import app.penny.servershared.dto.RefreshTokenRequest
import app.penny.servershared.dto.RefreshTokenResponse
import app.penny.services.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.authRoutes(
    authService: AuthService, jwtConfig: JwtConfig
) {

    post("/refreshToken") {
        val request = call.receive<RefreshTokenRequest>()
        val token = request.refreshToken
        val isRefreshTokenValid = authService.verifyToken(token)
        val userId = jwtConfig.getUserIdFromToken(token)
        if (isRefreshTokenValid) {
            val accessToken = jwtConfig.makeAccessToken(userId)
            val refreshToken = jwtConfig.makeRefreshToken(userId)
            call.respond(
                HttpStatusCode.OK, RefreshTokenResponse(
                    success = true, accessToken = accessToken, refreshToken = refreshToken
                )
            )
        } else {
            call.respond(
                HttpStatusCode.Unauthorized, RefreshTokenResponse(
                    success = false
                )
            )
        }
        return@post
    }


}