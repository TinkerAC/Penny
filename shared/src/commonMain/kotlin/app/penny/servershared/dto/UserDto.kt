package app.penny.servershared.dto

import app.penny.servershared.EditableField
import app.penny.servershared.enumerate.Action
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long = 0,
    val uuid: String = "",
    val username: String? = null,
    val email: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val passwordHash: String? = null,
) : BaseEntityDto(){
    override fun getEditableFields(): List<EditableField> {
        TODO("Not yet implemented")
    }

    override fun isCompleteFor(action: Action): Boolean {
        TODO("Not yet implemented")
    }
}
