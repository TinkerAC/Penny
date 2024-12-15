package app.penny.feature.aiChat

import app.penny.core.domain.model.ChatMessage

sealed class AIChatIntent {
    data class SendMessage(val message: String) : AIChatIntent()
    data class SendAudio(val audioFilePath: String, val duration: Long) : AIChatIntent()
    object LoadChatHistory : AIChatIntent()
    data class ConfirmPendingAction(
        val message: ChatMessage,
        val editableFields: Map<String, String?>
    ) : AIChatIntent()

    data class DismissFunctionalMessage(
        val message: ChatMessage
    ) : AIChatIntent()


}
