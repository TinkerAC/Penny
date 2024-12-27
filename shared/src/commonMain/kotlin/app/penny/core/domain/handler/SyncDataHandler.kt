package app.penny.core.domain.handler

import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.usecase.SyncDataUseCase
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.UserIntent.SyncData
import app.penny.servershared.enumerate.UserIntentStatus
import kotlin.uuid.ExperimentalUuidApi

/**
 * The handler for UserIntent[SyncData].
 */
class SyncDataHandler(
    private val syncDataUseCase: SyncDataUseCase
) : UserIntentHandler {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun handle(message: SystemMessage, dto: BaseEntityDto?): SystemMessage {

        syncDataUseCase()
        return message.copy(
            userIntent = message.userIntent.copy(
                status = UserIntentStatus.Completed,
            ),
            executeLog = "数据同步完成"
        )
    }

}