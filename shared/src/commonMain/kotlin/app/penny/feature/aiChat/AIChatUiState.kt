package app.penny.feature.aiChat

import app.penny.core.domain.model.ChatMessage

/**
 * AI聊天界面的UI状态。
 */
data class AIChatUiState(
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false
)
