package app.penny.feature.aiChat

//TODO:resize all the images,they are too big for their usage

import app.penny.core.data.model.MessageType
import app.penny.core.data.repository.ChatRepository
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.handler.ActionHandler
import app.penny.core.domain.handler.InsertLedgerHandler
import app.penny.core.domain.handler.InsertTransactionHandler
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.model.UserMessage
import app.penny.core.domain.model.UserModel
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.TransactionDto
import app.penny.servershared.enumerate.DtoAssociated
import app.penny.servershared.enumerate.UserIntent
import app.penny.servershared.enumerate.UserIntentStatus
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

    private val userIntentHandlers: Map<String, ActionHandler> = mapOf(
        UserIntent.InsertLedger::class.simpleName!! to InsertLedgerHandler(ledgerRepository),
        UserIntent.InsertTransaction::class.simpleName!! to InsertTransactionHandler(
            transactionRepository, ledgerRepository, userDataRepository
        )
    )

    init {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(
                user = userDataRepository.getUser()
            )
        }
    }

    fun handleIntent(intent: AIChatIntent) {
        when (intent) {
            is AIChatIntent.SendMessage -> sendMessage(intent.message)
            is AIChatIntent.SendAudio -> sendAudio(intent.audioFilePath, intent.duration)
            is AIChatIntent.ConfirmPendingAction -> confirmPendingAction(
                intent.message, intent.editableFields
            )

            is AIChatIntent.DismissFunctionalMessage -> dismissFunctionalMessage(intent.message)
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    private suspend fun loadChatHistory() {

        try {
            val messages = chatRepository.findChatHistoryByUserUuid(_uiState.value.user.uuid)
            _uiState.update { it.copy(messages = messages, isLoading = false) }
        } catch (e: Exception) {
            Logger.e("Failed to load chat history", e)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun sendMessage(messageText: String) {
        screenModelScope.launch {
            _uiState.update { it.copy(isSending = true) }
            try {

                val userMessage = UserMessage(
                    content = messageText,
                    type = MessageType.TEXT,
                    uuid = Uuid.random(),
                    timestamp = Clock.System.now().epochSeconds,
                    user = _uiState.value.user,
                    sender = _uiState.value.user,
                )
                addMessage(userMessage)

                _uiState.update { it.copy(inputText = "", isSending = false) }

                val aiReply = chatRepository.sendMessage(message = messageText)

                val aiMessage = SystemMessage(
                    type = MessageType.TEXT,
                    uuid = Uuid.random(),
                    timestamp = Clock.System.now().epochSeconds,
                    user = _uiState.value.user,
                    sender = UserModel.System,
                    userIntent = aiReply.userIntent,
                    content = aiReply.content
                )
                addMessage(aiMessage)

            } catch (e: Exception) {
                Logger.e("Failed to send message", e)
                _uiState.update { it.copy(isSending = false) }
            }
        }
    }

    private fun sendAudio(audioFilePath: String, duration: Long) {
        throw NotImplementedError("Audio messages are not supported yet")
    }

    private suspend fun executeAction(
        message: SystemMessage, userIntent: UserIntent, dto: BaseEntityDto
    ) {

        val handler = userIntentHandlers[userIntent::class.simpleName]
        if (handler != null) {
            try {
                handler.handle(userIntent, dto)
                val successMessage = "成功执行操作: ${userIntent.intentName}"
                message.content = successMessage
                message.userIntent!!.status = UserIntentStatus.Completed
                updateMessage(message)
            } catch (e: Exception) {
                Logger.e("Failed to execute userIntent: ${userIntent.intentName}", e)
                val failureMessage = "执行操作失败: ${userIntent.intentName}"
                val failureChatMessage = message.copy(
                    content = failureMessage,
                )
                updateMessage(failureChatMessage)
            }
        } else {
            Logger.e("No handler found for userIntent: ${userIntent.intentName}")
            val unknownActionMessage = "未知的操作: ${userIntent.intentName}"
            message.content = unknownActionMessage
            message.userIntent?.let { it.status = UserIntentStatus.Cancelled }
            updateMessage(message)
        }
    }

    private fun confirmPendingAction(message: SystemMessage, editedFields: Map<String, String?>) {
        when (message.userIntent) {
            is DtoAssociated -> {
                screenModelScope.launch {
                    val originalDto = message.userIntent.dto
                    if (originalDto == null) {
                        Logger.e("Original DTO is null for userIntent: ${message.userIntent?.intentName}")
                        return@launch
                    }

                    val newDto = rebuildDto(message.userIntent, originalDto, editedFields)
                    if (newDto != null && newDto.isCompleteFor(message.userIntent)) {
                        executeAction(message, message.userIntent, newDto)
                        val updatedMessage = message.copy(
                            userIntent = message.userIntent.copy(
                                dto = newDto, status = UserIntentStatus.Completed
                            )
                        )
                        updateMessage(updatedMessage)
                    } else {
                        Logger.e("DTO is still incomplete after editing")
                        val updatedMessage =
                            message.copy(userIntent = message.userIntent.copy(dto = newDto))
                        updateMessage(updatedMessage)
                    }
                }
            }

            else -> Logger.e("UserIntent does not implement DtoAssociated")
        }

    }

    private fun rebuildDto(
        userIntent: UserIntent?, originalDto: BaseEntityDto?, editedFields: Map<String, String?>
    ): BaseEntityDto? {
        if (userIntent == null || originalDto == null) return originalDto
        return when (userIntent) {
            is UserIntent.InsertLedger -> {
                val o = (originalDto as? LedgerDto) ?: LedgerDto(
                    userUuid = uiState.value.user.uuid.toString(),
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

            is UserIntent.InsertTransaction -> {
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

    private fun dismissFunctionalMessage(message: SystemMessage) {
        screenModelScope.launch {
            message.userIntent?.let { it.status = UserIntentStatus.Cancelled }
            updateMessage(message)
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
            it.copy(messages = it.messages.map { message ->
                if (message.uuid == updatedMessage.uuid) updatedMessage else message
            })
        }
    }
}
