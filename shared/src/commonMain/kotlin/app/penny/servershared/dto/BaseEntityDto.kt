package app.penny.servershared.dto

import app.penny.servershared.EditableField
import app.penny.servershared.enumerate.UserIntent
import kotlinx.serialization.Serializable

@Serializable
sealed class BaseEntityDto(
) {


    abstract fun getEditableFields(): List<EditableField>

    abstract fun isCompleteFor(userIntent: UserIntent): Boolean


}