package app.penny.core.domain.handler

import app.penny.core.domain.usecase.SyncDataUseCase
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.UserIntent
import app.penny.servershared.enumerate.UserIntent.SyncData

/**
 * The handler for UserIntent[SyncData].
 */
class SyncDataHandler(
    private val syncDataUseCase: SyncDataUseCase
) : ActionHandler {
    override suspend fun handle(userIntent: UserIntent, dto: BaseEntityDto?) {
        syncDataUseCase()
    }

}