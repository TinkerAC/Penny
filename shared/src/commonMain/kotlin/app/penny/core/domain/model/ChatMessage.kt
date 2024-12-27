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
    override val user: UserModel,
    override val sender: UserModel,
    override val type: MessageType,
    override val uuid: Uuid,
    override val timestamp: Long,
    override val content: String? = null,
    val audioFilePath: String? = null,
    val duration: Long? = null
) : ChatMessage(uuid, user, user, type, timestamp, content) {

}


@OptIn(ExperimentalUuidApi::class)
data class SystemMessage @ExperimentalUuidApi constructor(
    override val user: UserModel,
    override val type: MessageType,
    override val uuid: Uuid,
    override val timestamp: Long,
    override val sender: UserModel = UserModel.System,
    override var content: String? = null,
    val userIntent: UserIntent,
    val executeLog: String? = null
) : ChatMessage(uuid, user, sender, type, timestamp, content)



