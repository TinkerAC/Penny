package app.penny.servershared.dto.entityDto

import app.penny.servershared.dto.BaseEntityDto
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class LedgerDto(
    val userUuid: String,
    val uuid: String,
    val name: String,
    val coverUri: String? = null,
    val currencyCode: String,
    val createdAt: Long,
    val updatedAt: Long,
) : BaseEntityDto() {
    fun completedForAction(): Boolean {
        return name.isNotBlank() && currencyCode.isNotBlank()
    }
}