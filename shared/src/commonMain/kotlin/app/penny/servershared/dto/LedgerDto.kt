// file: shared/src/commonMain/kotlin/app/penny/servershared/dto/entityDto/LedgerDto.kt
package app.penny.servershared.dto

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
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
    override fun completedForAction(): Boolean {
        return name.isNotBlank() && currencyCode.isNotBlank()
    }

    override fun editableFields(): List<Pair<String, String?>> {
        return listOf(
            "name" to name,
            "currencyCode" to currencyCode
        )
    }

    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun create(
            userUuid: String,
            name: String,
            currencyCode: String
        ): LedgerDto {
            return LedgerDto(
                userUuid = userUuid,
                uuid = Uuid.random().toString(),
                name = name,
                currencyCode = currencyCode,
                createdAt = Clock.System.now().epochSeconds,
                updatedAt = Clock.System.now().epochSeconds
            )
        }
    }

}