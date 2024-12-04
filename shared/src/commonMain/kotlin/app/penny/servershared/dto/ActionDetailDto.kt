package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
sealed class ActionDetailDto(
    val action: String
) {

}