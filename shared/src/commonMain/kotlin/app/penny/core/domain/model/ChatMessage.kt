// file: shared/src/commonMain/kotlin/app/penny/core/domain/model/ChatMessage.kt
package app.penny.core.domain.model

import app.penny.core.data.model.MESSAGE_TYPE
import app.penny.servershared.enumerate.Action
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class ActionStatus {
    Pending,
    Completed,
    Cancelled
}

@OptIn(ExperimentalUuidApi::class)
data class ChatMessage(
    val type: MESSAGE_TYPE,
    val uuid: Uuid,
    val user: UserModel,
    val sender: UserModel,
    val timestamp: Long,
    val content: String? = null,
    val action: Action? = null,
    val audioFilePath: String? = null,
    val duration: Long? = null,
    val actionStatus: ActionStatus? = null
) {


}
