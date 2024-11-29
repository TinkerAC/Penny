// 文件：server/src/main/kotlin/app/penny/utils/Extensions.kt
package app.penny.utils

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

fun ApplicationCall.getUserId(): Long? {
    val principal = this.principal<JWTPrincipal>()
    val userId = principal?.getClaim("userId", String::class)
    return userId?.toLong()
}