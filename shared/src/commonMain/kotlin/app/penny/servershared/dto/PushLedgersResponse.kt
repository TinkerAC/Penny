package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class PushLedgersResponse(
    val success: Boolean,
    val changedLines: Int
)