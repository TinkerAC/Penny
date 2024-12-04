// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/model/ChatMessage.kt
package app.penny.core.domain.model

import app.penny.servershared.enumerate.Action
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
sealed class ChatMessage {

    abstract val uuid: Uuid
    abstract val user: UserModel
    abstract val sender: UserModel
    abstract val timestamp: Long
    abstract val action: Action?

    data class TextMessage(
        override val action: Action? = null,
        override val uuid: Uuid,
        override val user: UserModel,
        override val sender: UserModel,
        override val timestamp: Long,
        val content: String,
    ) : ChatMessage()

}