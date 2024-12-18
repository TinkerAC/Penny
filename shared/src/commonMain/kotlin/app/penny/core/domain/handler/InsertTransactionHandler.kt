// file: shared/src/commonMain/kotlin/app/penny/core/domain/handler/InsertTransactionHandler.kt
package app.penny.core.domain.handler

import app.penny.core.data.model.toModel
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.dto.TransactionDto
import app.penny.servershared.enumerate.Action
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * 处理插入交易动作的处理器。
 */
class InsertTransactionHandler(
    private val transactionRepository: TransactionRepository,
    private val ledgerRepository: LedgerRepository,
    private val userDataRepository: UserDataRepository
) : ActionHandler {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun handle(action: Action, dto: BaseEntityDto) {
        if (action !is Action.InsertTransaction) {
            throw IllegalArgumentException("Unsupported action type")
        }
        if (dto !is TransactionDto) {
            throw IllegalArgumentException("Invalid DTO type for InsertTransaction action")
        }


        // 确保 ledgerUuid 和 currencyCode 已设置
        dto.ledgerUuid =
            userDataRepository.getDefaultLedger()?.toString() ?: dto.ledgerUuid
        dto.currencyCode =
            ledgerRepository.findByUuid(Uuid.parse(dto.ledgerUuid))?.currency?.currencyCode
                ?: throw IllegalArgumentException("Currency code not found for ledger")


        // 插入交易
        try {
            val transaction = withContext(Dispatchers.Default) {
                transactionRepository.insert(dto.toModel())
            }
            Logger.i("Successfully inserted transaction: $transaction")

        } catch (e: Exception) {
            Logger.e("Failed to insert transaction: $dto", e)
            throw e
        }
    }
}
