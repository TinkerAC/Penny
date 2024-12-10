// file: shared/src/commonMain/kotlin/app/penny/servershared/dto/EditableField.kt
package app.penny.servershared

/**
 * 字段类型枚举，支持多种不同类型的字段。
 */
enum class FieldType {
    TEXT,
    DATE,
    CATEGORY,
    CURRENCY,
    // 可根据需求添加更多类型
}

/**
 * 可编辑字段的数据类，包含字段名称、标签、类型和值。
 */
data class EditableField(
    val name: String,
    val label: String,
    val type: FieldType,
    val value: String?
)
