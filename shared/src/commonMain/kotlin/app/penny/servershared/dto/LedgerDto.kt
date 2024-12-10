// file: shared/src/commonMain/kotlin/app/penny/servershared/dto/LedgerDto.kt
package app.penny.servershared.dto

import app.penny.servershared.EditableField
import app.penny.servershared.FieldType
import app.penny.servershared.enumerate.Action
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

    /**
     * 判断是否完成插入账本的动作。
     */
    override fun isCompleteFor(action: Action): Boolean {
        return when(action) {
            is Action.InsertLedger -> {
                name.isNotBlank() && currencyCode.isNotBlank()
            }
            // 其他动作可在此添加判断逻辑
            else -> false
        }
    }

    /**
     * 获取可编辑字段列表，包含字段类型。
     */
    override fun getEditableFields(): List<EditableField> {
        return listOf(
            EditableField("name", "名称", FieldType.TEXT, name),
            EditableField("currencyCode", "货币", FieldType.CURRENCY, currencyCode)
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
