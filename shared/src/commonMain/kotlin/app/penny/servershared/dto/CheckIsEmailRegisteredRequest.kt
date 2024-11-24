package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckIsEmailRegisteredRequest (
    val email: String
)