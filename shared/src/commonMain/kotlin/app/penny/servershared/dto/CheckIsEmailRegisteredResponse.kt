package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckIsEmailRegisteredResponse(
    val message: String = "",
    val isEmailRegistered: Boolean
)
