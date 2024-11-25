// file: server/src/main/kotlin/app/penny/services/AuthService.kt
package app.penny.services

import app.penny.config.JwtConfig
import com.auth0.jwt.exceptions.JWTVerificationException

class AuthService(
    jwtConfig: JwtConfig
) {
    // Implement authentication services here if needed
    fun verifyToken(token: String): Boolean {
        try {
            JwtConfig.verifier.verify(token)
            return true
        } catch (e: JWTVerificationException) {
            return false
        }
    }




}
