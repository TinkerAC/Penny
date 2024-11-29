package app.penny.core.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class UserModel(
    val uuid: Uuid = Uuid.fromLongs(0, 0),
    val username: String = "",
    val email: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
)