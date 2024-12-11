// file: shared/src/commonMain/kotlin/app/penny/servershared/dto/TransactionDto.kt
package app.penny.servershared.dto

import app.penny.core.domain.enum.Category
import app.penny.servershared.EditableField
import app.penny.servershared.FieldType
import app.penny.servershared.enumerate.Action
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class TransactionDto(
    val userId: Long,
    val uuid: String,
    var ledgerUuid: String,
    val transactionType: String,
    val transactionDate: Long,
    val categoryName: String,
    var currencyCode: String,
    val amount: String,
    val remark: String?,
    val createdAt: Long,
    val updatedAt: Long,
) : BaseEntityDto() {

    /**
     * 判断是否完成插入交易的动作。
     */
    override fun isCompleteFor(action: Action): Boolean {
        return when (action) {
            is Action.InsertTransaction -> {
                transactionType.isNotBlank() &&
                        transactionDate > 0 &&
                        categoryName.isNotBlank() &&
                        Category.valueOf(categoryName) in Category.getLevel2Categories() &&
                        amount.isNotBlank()
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
            EditableField("transactionType", "交易类型", FieldType.TEXT, transactionType),
            EditableField(
                "transactionDate",
                "交易日期",
                FieldType.DATE,
                transactionDate.toString()
            ),
            EditableField("categoryName", "分类", FieldType.CATEGORY, categoryName),
            EditableField("amount", "金额", FieldType.TEXT, amount),
            EditableField("remark", "备注", FieldType.TEXT, remark),
        )
    }
}
