package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class LedgerDto(
    val uuid: String,
    val name: String,
    val coverUri: String,
    val currencyCode: String,
    val createdAt: Long,
    val updatedAt: Long
)