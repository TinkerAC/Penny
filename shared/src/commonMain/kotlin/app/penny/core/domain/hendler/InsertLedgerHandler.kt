// file: shared/src/commonMain/kotlin/app/penny/core/domain/handler/InsertLedgerHandler.kt
package app.penny.core.domain.handler

import app.penny.core.data.model.toModel
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.domain.hendler.ActionHandler
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.enumerate.Action
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 处理插入账本动作的处理器。
 */
class InsertLedgerHandler(
    private val ledgerRepository: LedgerRepository
) : ActionHandler {

    override suspend fun handle(action: Action, dto: BaseEntityDto) {
        if (action !is Action.InsertLedger) {
            throw IllegalArgumentException("Unsupported action type")
        }
        if (dto !is LedgerDto) {
            throw IllegalArgumentException("Invalid DTO type for InsertLedger action")
        }

        // 插入账本
        val ledger = withContext(Dispatchers.Default) {
            ledgerRepository.insert(dto.toModel())
        }

        Logger.i("Successfully inserted ledger: $ledger")
        // 可在此处添加后续逻辑，如通知聊天系统
    }
}
