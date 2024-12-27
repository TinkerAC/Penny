// file: shared/src/commonMain/kotlin/app/penny/core/domain/handler/InsertTransactionHandler.kt
package app.penny.core.domain.handler

import app.penny.core.data.model.toModel
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.SystemMessage
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.dto.TransactionDto
import app.penny.servershared.enumerate.UserIntent
import co.touchlab.kermit.Logger
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * 处理插入交易动作的处理器。
 */
class InsertTransactionHandler(
    private val transactionRepository: TransactionRepository,
    private val ledgerRepository: LedgerRepository,
    private val userDataRepository: UserDataRepository
) : UserIntentHandler {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun handle(message: SystemMessage, dto: BaseEntityDto?): SystemMessage {
        if (message.userIntent !is UserIntent.InsertTransaction) {
            throw IllegalArgumentException("Unsupported userIntent type")
        }
        if (dto !is TransactionDto) {
            throw IllegalArgumentException("Invalid DTO type for InsertTransaction userIntent")
        }


        // 确保 ledgerUuid 和 currencyCode 已设置

        val defaultLedger: LedgerModel = userDataRepository.getDefaultLedger()

        dto.ledgerUuid = defaultLedger.uuid.toString()

        dto.currencyCode =
            ledgerRepository.findByUuid(Uuid.parse(dto.ledgerUuid))?.currency?.currencyCode
                ?: throw IllegalArgumentException("Currency code not found for ledger")


        // 插入交易
        try {
            val transaction = dto.toModel()
            transactionRepository.insert(transaction)
            Logger.d("Transaction inserted Successfully: $dto")

        } catch (e: Exception) {
            Logger.e("Failed to insert transaction: $dto", e)
            throw e
        }



        return message.copy()


    }
}
