// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/AIChatIntent.kt
package app.penny.feature.aiChat

sealed class AIChatIntent {
    data class SendMessage(val message: String) : AIChatIntent()
    data class SendAudio(val audioFilePath: String, val duration: Long) : AIChatIntent()
    data object LoadChatHistory : AIChatIntent()
}