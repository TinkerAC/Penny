// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/AIChatUiState.kt
package app.penny.feature.aiChat

import app.penny.core.domain.model.ChatMessage

data class AIChatUiState(
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false
)