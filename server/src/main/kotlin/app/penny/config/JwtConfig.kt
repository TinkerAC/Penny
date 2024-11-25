package app.penny.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException

object JwtConfig {
    private const val SECRET = "secret" //private key

    private const val ISSUER = "ktor.io"

    private const val ACCESS_TOKEN_VALIDITY_IN_MS = 60 * 30 * 1000 // 30 分钟
    private const val REFRESH_TOKEN_VALIDITY_IN_MS = 36_000_00 * 24 * 7 // 7 天
    private val algorithm = Algorithm.HMAC512(SECRET)

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .build()

    fun verifyToken(token: String): Boolean {
        try {
            verifier.verify(token)
            return true
        } catch (e: JWTVerificationException) {
            return false
        }
    }


    fun makeAccessToken(userId: Int): String = JWT.create()
        .withIssuer(ISSUER)
        .withClaim("userId", userId)
        .withExpiresAt(getExpiration(ACCESS_TOKEN_VALIDITY_IN_MS))
        .sign(algorithm)

    fun makeRefreshToken(userId: Int): String = JWT.create()
        .withIssuer(ISSUER)
        .withClaim("userId", userId)
        .withExpiresAt(getExpiration(REFRESH_TOKEN_VALIDITY_IN_MS))
        .sign(algorithm)


    fun getUserIdFromToken(token: String): Int {
        val jwt = verifier.verify(token)
        return jwt.getClaim("userId").asInt() ?: -1
    }


    private fun getExpiration(
        validityInMs: Int
    ) = java.util.Date(System.currentTimeMillis() + validityInMs)
}