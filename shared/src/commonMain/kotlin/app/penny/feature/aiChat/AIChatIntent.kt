package app.penny.feature.aiChat

import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.SystemMessage

sealed class AIChatIntent {
    data class SendMessage(val message: String) : AIChatIntent()
    data class SendAudio(val audioFilePath: String, val duration: Long) : AIChatIntent()
    data object ShowLedgerSelectDialog : AIChatIntent()
    data object HideLedgerSelectDialog : AIChatIntent()
    data class SelectLedger(val ledger: LedgerModel) : AIChatIntent()

    data class ConfirmPendingAction(
        val message: SystemMessage,
        val editableFields: Map<String, String?>
    ) : AIChatIntent()

    data class DismissFunctionalMessage(
        val message: SystemMessage
    ) : AIChatIntent()


}
