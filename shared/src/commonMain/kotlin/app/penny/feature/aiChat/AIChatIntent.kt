// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/AIChatIntent.kt
package app.penny.feature.aiChat

import app.penny.servershared.enumerate.Action

// 用户点击气泡确认后带回数据的Intent
sealed class AIChatIntent {
    data class SendMessage(val message: String) : AIChatIntent()
    data class SendAudio(val audioFilePath: String, val duration: Long) : AIChatIntent()
    object LoadChatHistory : AIChatIntent()
    data class ConfirmPendingAction(
        val action: Action,
        val editedFields: Map<String, String?>
    ) : AIChatIntent()
}