// file: shared/src/commonMain/kotlin/app/penny/core/domain/handler/InsertLedgerHandler.kt
package app.penny.core.domain.handler

import app.penny.core.data.model.toModel
import app.penny.core.data.repository.LedgerRepository
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.enumerate.UserIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 处理插入账本动作的处理器。
 */
class InsertLedgerHandler(
    private val ledgerRepository: LedgerRepository
) : ActionHandler {

    override suspend fun handle(userIntent: UserIntent, dto: BaseEntityDto?) {


        if (userIntent !is UserIntent.InsertLedger) {
            throw IllegalArgumentException("Unsupported userIntent type")
        }
        if (dto !is LedgerDto) {
            throw IllegalArgumentException("Invalid DTO type for InsertLedger userIntent")
        }


        // 插入账本
        val ledger = withContext(Dispatchers.Default) {
            ledgerRepository.insert(dto.toModel())
        }


    }
}
