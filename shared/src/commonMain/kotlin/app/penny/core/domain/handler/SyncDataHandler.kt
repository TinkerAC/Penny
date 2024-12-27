package app.penny.core.domain.handler

import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.usecase.SyncDataUseCase
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.UserIntent.SyncData

/**
 * The handler for UserIntent[SyncData].
 */
class SyncDataHandler(
    private val syncDataUseCase: SyncDataUseCase
) : UserIntentHandler {
    override suspend fun handle(message: SystemMessage, dto: BaseEntityDto?): SystemMessage {
        syncDataUseCase()
        return message
    }

}