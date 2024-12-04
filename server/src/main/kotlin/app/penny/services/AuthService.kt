// 文件：server/src/main/kotlin/app/penny/services/AuthService.kt
package app.penny.services

import app.penny.config.JwtConfig
import app.penny.servershared.dto.responseDto.RefreshTokenResponse
import com.auth0.jwt.exceptions.JWTVerificationException

class AuthService(
    private val jwtConfig: JwtConfig
) {

    private fun verifyRefreshToken(token: String): Boolean {
        return try {
            jwtConfig.refreshTokenVerifier.verify(token)
            true
        } catch (e: JWTVerificationException) {
            false
        }
    }

    private fun getAuthenticatedUserId(token: String): Long? {
        return try {
            jwtConfig.getUserIdFromRefreshToken(token)
        } catch (e: JWTVerificationException) {
            null
        }
    }

    fun refreshTokens(refreshToken: String): RefreshTokenResponse {
        val isRefreshTokenValid = verifyRefreshToken(refreshToken)
        if (isRefreshTokenValid) {
            val userId = getAuthenticatedUserId(refreshToken)
            if (userId != null) {
                val accessToken = jwtConfig.makeAccessToken(userId)
                val newRefreshToken = jwtConfig.makeRefreshToken(userId)
                return RefreshTokenResponse(
                    success = true,
                    message = "Token refreshed",
                    accessToken = accessToken,
                    refreshToken = newRefreshToken
                )
            }
        }
        return RefreshTokenResponse(
            success = false,
            message = "Invalid token"
        )
    }
}