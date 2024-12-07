// file: shared/src/commonMain/kotlin/app/penny/servershared/dto/entityDto/LedgerDto.kt
package app.penny.servershared.dto.entityDto

import app.penny.servershared.dto.BaseEntityDto
import kotlinx.serialization.Serializable

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
    override fun completedForAction(): Boolean {
        return name.isNotBlank() && currencyCode.isNotBlank()
    }

    override fun editableFields(): List<Pair<String, String?>> {
        return listOf(
            "name" to name,
            "currencyCode" to currencyCode
        )
    }
}