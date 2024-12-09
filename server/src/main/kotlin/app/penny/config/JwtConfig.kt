package app.penny.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException

object JwtConfig {
    private const val ACCESS_TOKEN_SECRET = "secret1"

    private const val REFRESH_TOKEN_SECRET = "secret2"

    private const val ISSUER = "ktor.io"

    private const val ACCESS_TOKEN_VALIDITY_IN_MS = 60 * 30 * 1000 // 30 分钟
    private const val REFRESH_TOKEN_VALIDITY_IN_MS = 36_000_00 * 24 * 7 // 7 天
    private val accessTokenAlgorithm = Algorithm.HMAC512(ACCESS_TOKEN_SECRET)
    private val refreshTokenAlgorithm = Algorithm.HMAC512(REFRESH_TOKEN_SECRET)

    val accessTokenVerifier: JWTVerifier = JWT.require(accessTokenAlgorithm)
        .withIssuer(ISSUER)
        .build()


    val refreshTokenVerifier: JWTVerifier = JWT.require(refreshTokenAlgorithm)
        .withIssuer(ISSUER)
        .build()


    fun verifyAccessToken(token: String): Boolean {
        try {
            accessTokenVerifier.verify(token)
            return true
        } catch (e: JWTVerificationException) {
            return false
        }
    }


    fun verifyRefreshToken(token: String): Boolean {
        try {
            refreshTokenVerifier.verify(token)
            return true
        } catch (e: JWTVerificationException) {
            return false
        }
    }


    fun makeAccessToken(userId: Long, userUuid: String): String =
        JWT.create()
            .withIssuer(ISSUER)
            .withClaim("userId", userId)
            .withClaim("userUuid", userUuid)
            .withExpiresAt(getExpiration(ACCESS_TOKEN_VALIDITY_IN_MS))
            .sign(accessTokenAlgorithm)

    fun makeRefreshToken(userId: Long, userUuid: String): String =
        JWT.create()
            .withIssuer(ISSUER)
            .withClaim("userId", userId)
            .withClaim("userUuid", userUuid)
            .withExpiresAt(getExpiration(REFRESH_TOKEN_VALIDITY_IN_MS))
            .sign(refreshTokenAlgorithm)


    fun getUserIdFromRefreshToken(token: String): Long {
        val jwt = refreshTokenVerifier.verify(token)
        return jwt.getClaim("userId").asLong()
    }


    private fun getExpiration(
        validityInMs: Int
    ) = java.util.Date(System.currentTimeMillis() + validityInMs)
}