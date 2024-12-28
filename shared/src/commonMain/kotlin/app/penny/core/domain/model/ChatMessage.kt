// file: shared/src/commonMain/kotlin/app/penny/core/domain/model/ChatMessage.kt
package app.penny.core.domain.model

import app.penny.core.data.model.MessageType
import app.penny.servershared.enumerate.UserIntent
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
sealed class ChatMessage(
    open val uuid: Uuid,
    open val user: UserModel, // the belonging user,
    open val sender: UserModel,
    open val type: MessageType,
    open val timestamp: Long,
    open val content: String?
)

@OptIn(ExperimentalUuidApi::class)
data class UserMessage @ExperimentalUuidApi constructor(
    override val user: UserModel, // which the message belongs to
    override val sender: UserModel, // the sender of the message
    override val type: MessageType, // the type of the message(Text,Audio(NYI))
    override val uuid: Uuid, // the unique id of the message
    override val timestamp: Long, // the timestamp of the message sent
    override val content: String? = null,  // the content of the message
    val duration: Long? = null // the duration of the audio message
) : ChatMessage(uuid, user, user, type, timestamp, content) {

}


@OptIn(ExperimentalUuidApi::class)
data class SystemMessage @ExperimentalUuidApi constructor(
    override val user: UserModel, // which the message belongs to
    override val type: MessageType, // the type of the message(Text)
    override val uuid: Uuid,  // the unique id of the message
    override val timestamp: Long, // the timestamp of the message sent
    override val sender: UserModel = UserModel.System, // the sender of the message
    override var content: String? = null,
    val userIntent: UserIntent, // the intent of the userIntent inferred from the last user message
    val executeLog: String? = null // the log of the execution of the userIntent
) : ChatMessage(uuid, user, sender, type, timestamp, content)



