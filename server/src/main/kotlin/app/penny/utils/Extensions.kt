// 文件：server/src/main/kotlin/app/penny/utils/Extensions.kt
package app.penny.utils

import app.penny.servershared.dto.entityDto.UserDto
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

fun ApplicationCall.getAuthedUser(): UserDto? {
    val principal = this.principal<JWTPrincipal>()

    val userId = principal?.getClaim("userId", String::class)
    val userUuid = principal?.getClaim("userUuid", String::class)

    return if (userId != null && userUuid != null) {
        UserDto(
            id = userId.toLong(),
            uuid = userUuid
        )
    } else {
        null
    }
}