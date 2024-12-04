package app.penny.servershared.dto

import app.penny.servershared.enumerate.Action
import kotlinx.serialization.Serializable


@Serializable
abstract class BaseActionDto() {
    abstract val action: Action
    abstract val parameters: Map<String, String>
}
