package app.penny.feature.aiChat

import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.SystemMessage

sealed class AIChatIntent {
    data class SendTextMessage(val message: String) : AIChatIntent()
    data object ShowLedgerSelectDialog : AIChatIntent()
    data object HideLedgerSelectDialog : AIChatIntent()
    data class SelectLedger(val ledger: LedgerModel) : AIChatIntent()

    data object ToggleInputMode : AIChatIntent()

    data object StartRecord : AIChatIntent()
    data object StopRecordAndSend : AIChatIntent()
    data object StopRecordAndDiscard : AIChatIntent()

    data class ConfirmPendingAction(
        val message: SystemMessage,
    ) : AIChatIntent()

    data class DismissFunctionalMessage(
        val message: SystemMessage
    ) : AIChatIntent()


}
