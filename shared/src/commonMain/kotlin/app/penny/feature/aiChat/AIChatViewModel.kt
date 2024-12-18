package app.penny.feature.aiChat

//TODO:resize all the images,they are too big for their usage

import app.penny.core.data.model.MESSAGE_TYPE
import app.penny.core.data.repository.ChatRepository
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.handler.ActionHandler
import app.penny.core.domain.handler.InsertLedgerHandler
import app.penny.core.domain.handler.InsertTransactionHandler
import app.penny.core.domain.model.ActionStatus
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.UserModel
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.TransactionDto
import app.penny.servershared.enumerate.Action
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AIChatViewModel(
    private val chatRepository: ChatRepository,
    private val userDataRepository: UserDataRepository,
    private val userRepository: UserRepository,
    private val ledgerRepository: LedgerRepository,
    private val transactionRepository: TransactionRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(AIChatUiState())
    val uiState: StateFlow<AIChatUiState> = _uiState.asStateFlow()

    private lateinit var currentUser: UserModel

    private val actionHandlers: Map<String, ActionHandler> = mapOf(
        Action.InsertLedger::class.simpleName!! to InsertLedgerHandler(ledgerRepository),
        Action.InsertTransaction::class.simpleName!! to InsertTransactionHandler(
            transactionRepository,
            ledgerRepository,
            userDataRepository
        )
    )

    init {
        screenModelScope.launch {
            currentUser = userDataRepository.getUser()
            handleIntent(AIChatIntent.LoadChatHistory)
        }
    }

    fun handleIntent(intent: AIChatIntent) {
        when (intent) {
            is AIChatIntent.LoadChatHistory -> loadChatHistory()
            is AIChatIntent.SendMessage -> sendMessage(intent.message)
            is AIChatIntent.SendAudio -> sendAudio(intent.audioFilePath, intent.duration)
            is AIChatIntent.ConfirmPendingAction -> confirmPendingAction(
                intent.message,
                intent.editableFields
            )

            is AIChatIntent.DismissFunctionalMessage -> dismissFunctionalMessage(intent.message)
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    private fun loadChatHistory() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val messages = chatRepository.findChatHistoryByUserUuid(currentUser.uuid)
                _uiState.update { it.copy(messages = messages, isLoading = false) }
            } catch (e: Exception) {
                Logger.e("Failed to load chat history", e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun sendMessage(message: String) {
        screenModelScope.launch {
            _uiState.update { it.copy(isSending = true) }
            try {
                val userMessage = ChatMessage(
                    uuid = Uuid.random(),
                    type = MESSAGE_TYPE.TEXT,
                    user = currentUser,
                    sender = currentUser,
                    timestamp = Clock.System.now().epochSeconds,
                    content = message
                )
                addMessage(userMessage)

                _uiState.update { it.copy(inputText = "", isSending = false) }

                val aiReply = chatRepository.sendMessage(message = message)

                val aiMessage = ChatMessage(
                    uuid = Uuid.random(),
                    user = currentUser,
                    type = MESSAGE_TYPE.TEXT,
                    sender = UserModel.AI,
                    timestamp = Clock.System.now().epochSeconds,
                    content = aiReply.message,
                    action = aiReply.action,
                    actionStatus = if (aiReply.action != null) ActionStatus.Pending else ActionStatus.Completed
                )
                addMessage(aiMessage)
//                if (aiReply.success && aiReply.action != null) {
//                    handleAction(aiMessage)
//                }
            } catch (e: Exception) {
                Logger.e("Failed to send message", e)
                _uiState.update { it.copy(isSending = false) }
            }
        }
    }

    private fun sendAudio(audioFilePath: String, duration: Long) {
        throw NotImplementedError("Audio messages are not supported yet")
    }

    private fun handleAction(message: ChatMessage) {
        screenModelScope.launch {
            val action = message.action
            val dto = action?.dto

            if (action == null || dto == null) {
                Logger.e("Action or DTO is null for message: ${message.uuid}")
                return@launch
            }

            if (dto.isCompleteFor(action)) {
                executeAction(message, action, dto)
            } else {
                // The action is pending, waiting for user input
                // Already persisted as ChatMessage with ActionStatus.Pending
                // UI will display the message and allow user to confirm or dismiss
            }
        }
    }

    private suspend fun executeAction(message: ChatMessage, action: Action, dto: BaseEntityDto) {
        val handler = actionHandlers[action::class.simpleName]
        if (handler != null) {
            try {
                handler.handle(action, dto)
                val successMessage = "成功执行操作: ${action.actionName}"
                val successChatMessage = message.copy(
                    content = successMessage,
                    actionStatus = ActionStatus.Completed
                )
                updateMessage(successChatMessage)
            } catch (e: Exception) {
                Logger.e("Failed to execute action: ${action.actionName}", e)
                val failureMessage = "执行操作失败: ${action.actionName}"
                val failureChatMessage = message.copy(
                    content = failureMessage,
                )
                updateMessage(failureChatMessage)
            }
        } else {
            Logger.e("No handler found for action: ${action.actionName}")
            val unknownActionMessage = "未知的操作: ${action.actionName}"
            val unknownChatMessage = message.copy(
                content = unknownActionMessage,
                actionStatus = ActionStatus.Cancelled
            )
            updateMessage(unknownChatMessage)
        }
    }

    private fun confirmPendingAction(message: ChatMessage, editedFields: Map<String, String?>) {
        screenModelScope.launch {
            val originalDto = message.action?.dto
            if (originalDto == null) {
                Logger.e("Original DTO is null for action: ${message.action?.actionName}")
                return@launch
            }

            val newDto = rebuildDto(message.action, originalDto, editedFields)
            if (newDto != null && newDto.isCompleteFor(message.action)) {
                executeAction(message, message.action, newDto)
                val updatedMessage = message.copy(
                    action = message.action,
                    actionStatus = ActionStatus.Completed
                )
                updateMessage(updatedMessage)
            } else {
                Logger.e("DTO is still incomplete after editing")
                val updatedMessage = message.copy(action = message.action.copy(dto = newDto))
                updateMessage(updatedMessage)
            }
        }
    }

    private fun rebuildDto(
        action: Action?,
        originalDto: BaseEntityDto?,
        editedFields: Map<String, String?>
    ): BaseEntityDto? {
        if (action == null || originalDto == null) return originalDto
        return when (action.actionName) {
            "InsertLedger" -> {
                val o = (originalDto as? LedgerDto) ?: LedgerDto(
                    userUuid = currentUser.uuid.toString(),
                    uuid = Uuid.random().toString(),
                    name = "",
                    currencyCode = "",
                    createdAt = Clock.System.now().epochSeconds,
                    updatedAt = Clock.System.now().epochSeconds
                )
                o.copy(
                    name = editedFields["name"] ?: o.name,
                    currencyCode = editedFields["currencyCode"] ?: o.currencyCode
                )
            }

            "InsertTransaction" -> {
                val o = (originalDto as? TransactionDto) ?: TransactionDto(
                    userId = 0L,
                    uuid = Uuid.random().toString(),
                    ledgerUuid = "",
                    transactionType = "",
                    transactionDate = 0L,
                    categoryName = "",
                    currencyCode = "",
                    amount = "",
                    remark = null,
                    createdAt = Clock.System.now().epochSeconds,
                    updatedAt = Clock.System.now().epochSeconds
                )
                o.copy(
                    transactionType = editedFields["transactionType"] ?: o.transactionType,
                    transactionDate = editedFields["transactionDate"]?.toLongOrNull()
                        ?: o.transactionDate,
                    categoryName = editedFields["categoryName"] ?: o.categoryName,
                    amount = editedFields["amount"] ?: o.amount,
                    remark = editedFields["remark"] ?: o.remark,
                    ledgerUuid = editedFields["ledgerUuid"] ?: o.ledgerUuid,
                    currencyCode = editedFields["currencyCode"] ?: o.currencyCode
                )
            }

            else -> originalDto
        }
    }

    private fun dismissFunctionalMessage(message: ChatMessage) {
        screenModelScope.launch {
            val updatedMessage = message.copy(actionStatus = ActionStatus.Cancelled)
            updateMessage(updatedMessage)
        }
    }

    /**
     * 辅助函数：添加消息到仓库并更新UI状态
     */
    private suspend fun addMessage(chatMessage: ChatMessage) {
        chatRepository.insert(chatMessage)
        _uiState.update { it.copy(messages = it.messages + chatMessage) }
    }

    /**
     * 辅助函数：更新消息在仓库中并同步UI状态
     */
    private suspend fun updateMessage(updatedMessage: ChatMessage) {
        chatRepository.update(updatedMessage)
        _uiState.update {
            it.copy(
                messages = it.messages.map { message ->
                    if (message.uuid == updatedMessage.uuid) updatedMessage else message
                }
            )
        }
    }
}
