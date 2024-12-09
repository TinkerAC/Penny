package app.penny.servershared.dto

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@Serializable
sealed class BaseEntityDto(
) {


    open fun completedForAction(): Boolean {
        return true
    }

    open fun editableFields(): List<Pair<String, String?>> {
        return emptyList()
    }



}