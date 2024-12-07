package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
abstract class BaseEntityDto(
) {


    open fun completedForAction(): Boolean {
        return true
    }

    open fun editableFields(): List<Pair<String, String?>> {
        return emptyList()
    }

}