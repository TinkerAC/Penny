// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/AIChatUiState.kt
package app.penny.feature.aiChat

import app.penny.core.domain.model.ChatMessage
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.Action

// UIState中加入pendingAction和pendingDto用于显示FunctionalBubble
data class AIChatUiState(
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,

    // 当action需要用户确认时显示的气泡
    val showFunctionalBubble: Boolean = false,
    val pendingAction: Action? = null,
    val pendingDto: BaseEntityDto? = null
)