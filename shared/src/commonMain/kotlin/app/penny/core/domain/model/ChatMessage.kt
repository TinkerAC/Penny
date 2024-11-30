// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/model/ChatMessage.kt
package app.penny.core.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
sealed class ChatMessage {

    abstract val uuid: Uuid
    abstract val user: UserModel
    abstract val sender: UserModel
    abstract val timestamp: Long

    data class TextMessage(
        override val uuid: Uuid,
        override val user: UserModel,
        override val sender: UserModel,
        val content: String,
        override val timestamp: Long
    ) : ChatMessage()

    data class AudioMessage(
        override val uuid: Uuid,
        override val user: UserModel,
        override val sender: UserModel,
        val audioFilePath: String,
        val duration: Long,
        override val timestamp: Long
    ) : ChatMessage()
}