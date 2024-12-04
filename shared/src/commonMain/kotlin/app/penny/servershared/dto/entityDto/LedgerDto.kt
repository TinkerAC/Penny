package app.penny.servershared.dto.entityDto

import app.penny.servershared.dto.BaseEntityDto
import kotlinx.serialization.Serializable

@Serializable
data class LedgerDto(
    val userId: Long,
    val uuid: String,
    val name: String,
    val coverUri: String = "", // nyi
    val currencyCode: String,
    val createdAt: Long,
    val updatedAt: Long
):BaseEntityDto()