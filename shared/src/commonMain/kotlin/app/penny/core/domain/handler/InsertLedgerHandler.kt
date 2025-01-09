// file: shared/src/commonMain/kotlin/app/penny/core/domain/handler/InsertLedgerHandler.kt
package app.penny.core.domain.handler

import app.penny.core.data.enumerate.toModel
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.domain.model.SystemMessage
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.enumerate.UserIntent
import app.penny.servershared.enumerate.UserIntentStatus
import kotlin.uuid.ExperimentalUuidApi

/**
 * 处理插入账本动作的处理器。
 */
class InsertLedgerHandler(
    private val ledgerRepository: LedgerRepository
) : UserIntentHandler {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun handle(message: SystemMessage, dto: BaseEntityDto?): SystemMessage {
        if (message.userIntent !is UserIntent.InsertLedger) {
            throw IllegalArgumentException("Unsupported userIntent type")
        }
        if (dto !is LedgerDto) {
            throw IllegalArgumentException("Invalid DTO type for InsertLedger userIntent , expected LedgerDto , but got $dto")
        }

        val ledgerToBeInserted = dto.toModel()
        ledgerRepository.insert(ledgerToBeInserted)


        return message.copy(
            userIntent = message.userIntent.copy(
                status = UserIntentStatus.Completed,

                ),
            executeLog = "成功插入账本：${ledgerToBeInserted.name}"
        )

    }
}
