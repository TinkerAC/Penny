// file: shared/src/commonMain/kotlin/app/penny/servershared/dto/TransactionDto.kt
package app.penny.servershared.dto

import app.penny.core.domain.enumerate.Category
import app.penny.servershared.EditableField
import app.penny.servershared.FieldType
import app.penny.servershared.enumerate.UserIntent
import kotlinx.serialization.Serializable

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
    override fun isCompleteFor(userIntent: UserIntent): Boolean {
        return when (userIntent) {
            is UserIntent.InsertTransaction -> {
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
            EditableField(
                "transactionType",
                "Transaction Type",
                FieldType.TEXT, transactionType
            ),
            EditableField(
                "transactionDate",
                "Transaction Date",
                FieldType.DATE,
                transactionDate.toString()
            ),
            EditableField(
                "categoryName", "Category",
                FieldType.CATEGORY, categoryName
            ),
            EditableField(
                "amount", "Amount",
                FieldType.TEXT, amount
            ),
            EditableField(
                "remark", "Remark",
                FieldType.TEXT, remark
            ),
        )
    }
}
