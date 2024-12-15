package app.penny.core.utils

import io.ktor.util.decodeBase64Bytes
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

object JwtUtils {

    private const val JWT_SEPARATOR = "."

    fun parseJwtPayload(token: String): JsonObject {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token format")
        }
        val payloadBase64 = parts[1]
        val payloadJson = decodeBase64Url(payloadBase64)
        return Json.parseToJsonElement(payloadJson).jsonObject
    }

    fun getExpirationTime(token: String): Instant {
        return try {
            val jwtPayload = parseJwtPayload(token)
            val exp = jwtPayload["exp"]?.jsonPrimitive?.longOrNull
                ?: throw IllegalStateException("Token does not have an expiration time")
            // `exp` 是自 Unix 纪元以来的秒数
            Instant.fromEpochSeconds(exp)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to decode token", e)
        }
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            val expirationTime = getExpirationTime(token)
            val currentTime = Clock.System.now()
            currentTime >= expirationTime
        } catch (e: Exception) {
            true // 解析失败，认为 Token 已过期
        }
    }

    fun decodeBase64Url(base64UrlString: String): String {
        val base64String = base64UrlString.replace('-', '+').replace('_', '/')
            .let {
                // 填充 `=` 使字符串长度是 4 的倍数
                val padding = (4 - it.length % 4) % 4
                it + "=".repeat(padding)
            }
        val decodedBytes = base64String.decodeBase64Bytes()
        return decodedBytes.decodeToString()
    }
}