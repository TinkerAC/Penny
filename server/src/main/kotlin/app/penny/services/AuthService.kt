// file: server/src/main/kotlin/app/penny/services/AuthService.kt
package app.penny.services

import app.penny.config.JwtConfig
import com.auth0.jwt.exceptions.JWTVerificationException

class AuthService(
    jwtConfig: JwtConfig
) {
    // Implement authentication services here if needed
    fun verifyRefreshToken(token: String): Boolean {
        try {
            JwtConfig.refreshTokenVerifier.verify(token)
            return true
        } catch (e: JWTVerificationException) {
            return false
        }
    }



    fun getAuthedUserId(token: String): Int {
        return JwtConfig.getUserIdFromRefreshToken(token)
    }


}
