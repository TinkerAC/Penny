package app.penny.core.domain.handler

import app.penny.core.domain.model.SystemMessage
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.UserIntent

class UserIntentHandlers(
    private val insertLedgerHandler: InsertLedgerHandler,
    private val insertTransactionHandler: InsertTransactionHandler,
    private val justTalkHandler: JustTalkHandler,
    private val syncDataHandler: SyncDataHandler,
    private val generateMonthlyReportHandler: GenerateMonthlyReportHandler
) {
    suspend fun handle(message: SystemMessage, dto: BaseEntityDto? = null): SystemMessage {
        return when (message.userIntent) {
            is UserIntent.InsertLedger -> insertLedgerHandler.handle(message, dto)
            is UserIntent.InsertTransaction -> insertTransactionHandler.handle(message, dto)
            is UserIntent.JustTalk -> justTalkHandler.handle(message, dto)
            is UserIntent.SyncData -> syncDataHandler.handle(message, dto)
            is UserIntent.GenerateMonthlyReport -> generateMonthlyReportHandler.handle(message)
            else -> {
                throw IllegalArgumentException("Unsupported userIntent type or not implemented")
            }
        }
    }

}