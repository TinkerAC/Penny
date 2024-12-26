package app.penny.servershared.dto

import app.penny.servershared.enumerate.UserIntent
import kotlinx.serialization.Serializable


@Serializable
abstract class BaseActionDto() {
    abstract val userIntent: UserIntent
    abstract val parameters: Map<String, String>
}
