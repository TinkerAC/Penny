package app.penny.feature.aiChat

import app.penny.core.data.enumerate.MessageType
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.UserModel

/**
 * AI聊天界面的UI状态。
 */
data class AIChatUiState(
    val user: UserModel = UserModel.Uninitialized,
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val ledgerSelectDialogVisible: Boolean = false,
    val selectedLedger: LedgerModel? = null,
    val ledgerList: List<LedgerModel> = emptyList(),
    val userAvatarUrl: String = "",
    val isRecording: Boolean = false,
    val inputMode: MessageType = MessageType.TEXT
)
