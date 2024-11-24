package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckIsEmailRegisteredResponse(
    val isEmailRegistered: Boolean
)
