package app.penny.core.domain.handler

import app.penny.core.domain.model.SystemMessage
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.UserIntent
import kotlin.uuid.ExperimentalUuidApi

class JustTalkHandler : UserIntentHandler {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun handle(message: SystemMessage, dto: BaseEntityDto?): SystemMessage {

        if (message.userIntent !is UserIntent.JustTalk) {
            throw IllegalArgumentException("Unsupported userIntent type")
        }

        return message.copy(
            content = message.userIntent.aiReplyText
        )

    }
}