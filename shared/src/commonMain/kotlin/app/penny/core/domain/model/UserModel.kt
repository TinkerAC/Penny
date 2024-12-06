package app.penny.core.domain.model

import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class UserModel(
    var uuid: Uuid = Uuid.fromLongs(0, 0),
    val username: String = "",
    val email: String? = null,
    val createdAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST
) {
    companion object {
        val AI = UserModel(
            uuid = Uuid.fromLongs(0, 0),
            username = "AI",
            email = "",
        )

    }
}