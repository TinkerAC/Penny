package app.penny.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

object JwtConfig {
    private const val SECRET = "secret" // 需要替换为安全的密钥

    private const val ISSUER = "ktor.io"
    private const val VALIDITY_IN_MS = 36_000_00 * 24 // 24 小时

    private val algorithm = Algorithm.HMAC512(SECRET)

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .build()

    fun makeToken(userId: Int): String = JWT.create()
        .withIssuer(ISSUER)
        .withClaim("userId", userId)
        .withExpiresAt(getExpiration())
        .sign(algorithm)



    fun getUserIdFromToken(token: String): Int {
        val jwt = verifier.verify(token)
        return jwt.getClaim("userId").asInt() ?: -1
    }


    private fun getExpiration() = java.util.Date(System.currentTimeMillis() + VALIDITY_IN_MS)
}