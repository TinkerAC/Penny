package app.penny.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

object JwtConfig {
    private const val secret = "secret" // 需要替换为安全的密钥
    private const val issuer = "ktor.io"
    private const val validityInMs = 36_000_00 * 24 // 24 小时

    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()

    fun makeToken(userId: Int): String = JWT.create()
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = java.util.Date(System.currentTimeMillis() + validityInMs)
}