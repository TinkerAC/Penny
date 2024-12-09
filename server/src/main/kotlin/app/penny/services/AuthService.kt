// 文件：server/src/main/kotlin/app/penny/services/AuthService.kt
package app.penny.services

import app.penny.config.JwtConfig
import app.penny.repository.UserRepository
import app.penny.servershared.dto.UserDto
import app.penny.servershared.dto.responseDto.RefreshTokenResponse
import com.auth0.jwt.exceptions.JWTVerificationException

class AuthService(
    private val jwtConfig: JwtConfig,
    private val userRepository: UserRepository
) {

    private fun verifyRefreshToken(token: String): Boolean {
        return try {
            jwtConfig.refreshTokenVerifier.verify(token)
            true
        } catch (e: JWTVerificationException) {
            false
        }
    }

    private fun getAuthedUser(token: String): UserDto? {
        return try {
            val jwt = jwtConfig.refreshTokenVerifier.verify(token)

            val userId = jwt.getClaim("userId").asLong()

            val user = userRepository.findById(userId)

            return user
        } catch (e: JWTVerificationException) {
            null
        }
    }

    fun refreshTokens(refreshToken: String): RefreshTokenResponse {
        val isRefreshTokenValid = verifyRefreshToken(refreshToken)
        if (isRefreshTokenValid) {
            val authedUser = getAuthedUser(refreshToken)
            if (authedUser != null) {
                val accessToken = jwtConfig.makeAccessToken(authedUser.id, authedUser.uuid)
                val newRefreshToken = jwtConfig.makeRefreshToken(authedUser.id, authedUser.uuid)
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