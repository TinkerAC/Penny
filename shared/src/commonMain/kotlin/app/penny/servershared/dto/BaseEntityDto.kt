package app.penny.servershared.dto

import app.penny.servershared.EditableField
import app.penny.servershared.enumerate.Action
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@Serializable
sealed class BaseEntityDto(
) {


    abstract fun getEditableFields(): List<EditableField>

    abstract fun isCompleteFor(action: Action): Boolean



}