// file: server/src/main/kotlin/app/penny/utils/UserAttributes.kt
package app.penny.utils

import app.penny.servershared.dto.entityDto.UserDto
import io.ktor.server.application.ApplicationCall
import io.ktor.util.*

/**
 * AttributeKey to store UserDto in ApplicationCall attributes.
 */
val UserKey = AttributeKey<UserDto>("User")

/**
 * Extension function to retrieve the current user from ApplicationCall.
 */
fun ApplicationCall.getUser(): UserDto? = attributes.getOrNull(UserKey)
