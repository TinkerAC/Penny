// file: shared/src/commonMain/kotlin/app/penny/servershared/dto/EditableField.kt
package app.penny.servershared

/**
 * Enum class representing the types of fields that can be edited.
 */
enum class FieldType {
    TEXT,
    DATE,
    CATEGORY,
    CURRENCY,
}

/**
 * Represents a field that can be edited.
 */
data class EditableField(
    val name: String,
    val label: String,
    val type: FieldType,
    val value: String?
)
