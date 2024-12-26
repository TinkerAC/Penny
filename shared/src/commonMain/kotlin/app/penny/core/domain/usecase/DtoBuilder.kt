package app.penny.core.domain.usecase

import app.penny.core.domain.model.UserModel
import app.penny.servershared.dto.*
import app.penny.servershared.enumerate.UserIntent
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class RebuildDtoUseCase {
    @OptIn(ExperimentalUuidApi::class)
    fun execute(
        user: UserModel,
        userIntent: UserIntent?,
        originalDto: BaseEntityDto?,
        editedFields: Map<String, String?>
    ): BaseEntityDto? {
        if (userIntent == null || originalDto == null) return originalDto

        return when (userIntent) {
            is UserIntent.InsertLedger -> {
                val o = (originalDto as? LedgerDto) ?: LedgerDto(
                    userUuid = user.uuid.toString(),
                    uuid = Uuid.random().toString(),
                    name = "",
                    currencyCode = "",
                    createdAt = Clock.System.now().epochSeconds,
                    updatedAt = Clock.System.now().epochSeconds
                )
                o.copy(
                    name = editedFields["name"] ?: o.name,
                    currencyCode = editedFields["currencyCode"] ?: o.currencyCode
                )
            }

            is UserIntent.InsertTransaction -> {
                val o = (originalDto as? TransactionDto) ?: TransactionDto(
                    userId = 0L,
                    uuid = Uuid.random().toString(),
                    ledgerUuid = "",
                    transactionType = "",
                    transactionDate = 0L,
                    categoryName = "",
                    currencyCode = "",
                    amount = "",
                    remark = null,
                    createdAt = Clock.System.now().epochSeconds,
                    updatedAt = Clock.System.now().epochSeconds
                )
                o.copy(
                    transactionType = editedFields["transactionType"] ?: o.transactionType,
                    transactionDate = editedFields["transactionDate"]?.toLongOrNull()
                        ?: o.transactionDate,
                    categoryName = editedFields["categoryName"] ?: o.categoryName,
                    amount = editedFields["amount"] ?: o.amount,
                    remark = editedFields["remark"] ?: o.remark,
                    ledgerUuid = editedFields["ledgerUuid"] ?: o.ledgerUuid,
                    currencyCode = editedFields["currencyCode"] ?: o.currencyCode
                )
            }

            else -> originalDto
        }
    }
}