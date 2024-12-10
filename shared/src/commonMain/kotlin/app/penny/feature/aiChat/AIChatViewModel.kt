// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/AIChatViewModel.kt
package app.penny.feature.aiChat

import app.penny.core.domain.handler.InsertLedgerHandler
import app.penny.core.domain.hendler.InsertTransactionHandler
import app.penny.core.domain.model.UserModel
import app.penny.core.data.repository.ChatRepository
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.hendler.ActionHandler
import app.penny.core.domain.model.ChatMessage
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

    // 动作处理器注册表
    private val actionHandlers: Map<String, ActionHandler>

    init {
        // 初始化动作处理器
        actionHandlers = mapOf(
            Action.InsertLedger::class.simpleName!! to InsertLedgerHandler(ledgerRepository),
            Action.InsertTransaction::class.simpleName!! to InsertTransactionHandler(
                transactionRepository,
                ledgerRepository,
                userDataRepository
            )
        )

        screenModelScope.launch {
            currentUser = userRepository.findByUuid(userDataRepository.getUserUuid())
                ?: throw IllegalStateException("User not found")
            handleIntent(AIChatIntent.LoadChatHistory)
        }
    }

    fun handleIntent(intent: AIChatIntent) {
        when (intent) {
            is AIChatIntent.LoadChatHistory -> loadChatHistory()
            is AIChatIntent.SendMessage -> sendMessage(intent.message)
            is AIChatIntent.SendAudio -> sendAudio(intent.audioFilePath, intent.duration)
            is AIChatIntent.ConfirmPendingAction -> confirmPendingAction(
                intent.action,
                intent.editedFields
            )
        }
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
                val chatMessage = ChatMessage.TextMessage(
                    uuid = Uuid.random(),
                    user = currentUser,
                    sender = currentUser,
                    timestamp = Clock.System.now().epochSeconds,
                    content = message
                )
                chatRepository.saveChatMessage(chatMessage)
                _uiState.update {
                    it.copy(
                        messages = it.messages + chatMessage,
                        inputText = "",
                        isSending = false
                    )
                }

                val aiReply = chatRepository.sendMessage(message = message)

                if (aiReply.success && aiReply.action != null) {
                    handleAction(aiReply.action)
                }

                val cm = ChatMessage.TextMessage(
                    uuid = Uuid.random(),
                    user = currentUser,
                    sender = UserModel.AI,
                    timestamp = Clock.System.now().epochSeconds,
                    content = aiReply.message
                )

                _uiState.update {
                    it.copy(
                        messages = it.messages + cm
                    )
                }

            } catch (e: Exception) {
                Logger.e("Failed to send message", e)
                _uiState.update { it.copy(isSending = false) }
            }
        }
    }

    private fun sendAudio(audioFilePath: String, duration: Long) {
        throw NotImplementedError("Audio messages are not supported yet")
    }

    private fun handleAction(action: Action) {
        screenModelScope.launch {
            val dto = action.dto

            if (dto == null) {
                Logger.e("Action DTO is null for action: ${action.actionName}")
                return@launch
            }

            if (dto.isCompleteFor(action)) {
                executeAction(action, dto)
            } else {
                _uiState.update {
                    it.copy(
                        showFunctionalBubble = true,
                        pendingAction = action,
                        pendingDto = dto
                    )
                }
            }
        }
    }

    private suspend fun executeAction(action: Action, dto: BaseEntityDto) {
        val handler = actionHandlers[action::class.simpleName]
        if (handler != null) {
            try {
                handler.handle(action, dto)
                val successMessage = "Successfully executed action: ${action.actionName}"
                val chatMessage = ChatMessage.TextMessage(
                    uuid = Uuid.random(),
                    user = currentUser,
                    sender = UserModel.AI,
                    timestamp = Clock.System.now().epochSeconds,
                    content = successMessage
                )
                chatRepository.saveChatMessage(chatMessage)
                _uiState.update {
                    it.copy(
                        messages = it.messages + chatMessage,
                        showFunctionalBubble = false,
                        pendingAction = null,
                        pendingDto = null
                    )
                }
            } catch (e: Exception) {
                Logger.e("Failed to execute action: ${action.actionName}", e)
                val failureMessage = "Failed to execute action: ${action.actionName}"
                val chatMessage = ChatMessage.TextMessage(
                    uuid = Uuid.random(),
                    user = currentUser,
                    sender = UserModel.AI,
                    timestamp = Clock.System.now().epochSeconds,
                    content = failureMessage
                )
                chatRepository.saveChatMessage(chatMessage)
                _uiState.update {
                    it.copy(
                        messages = it.messages + chatMessage,
                        showFunctionalBubble = false,
                        pendingAction = null,
                        pendingDto = null
                    )
                }
            }
        } else {
            Logger.e("No handler found for action: ${action.actionName}")
            val unknownActionMessage = "Unknown action: ${action.actionName}"
            val chatMessage = ChatMessage.TextMessage(
                uuid = Uuid.random(),
                user = currentUser,
                sender = UserModel.AI,
                timestamp = Clock.System.now().epochSeconds,
                content = unknownActionMessage
            )
            chatRepository.saveChatMessage(chatMessage)
            _uiState.update {
                it.copy(
                    messages = it.messages + chatMessage,
                    showFunctionalBubble = false,
                    pendingAction = null,
                    pendingDto = null
                )
            }
        }
    }

    private fun confirmPendingAction(action: Action, editedFields: Map<String, String?>) {
        screenModelScope.launch {
            val originalDto = _uiState.value.pendingDto
            if (originalDto == null) {
                Logger.e("Pending DTO is null while confirming action")
                return@launch
            }
            val newDto = rebuildDto(action, originalDto, editedFields)
            if (newDto != null && newDto.isCompleteFor(action)) {
                executeAction(action, newDto)
            } else {
                Logger.e("DTO is still incomplete after editing")
                _uiState.update {
                    it.copy(
                        pendingDto = newDto ?: originalDto
                    )
                }
            }
        }
    }

    private fun rebuildDto(
        action: Action,
        originalDto: BaseEntityDto?,
        editedFields: Map<String, String?>
    ): BaseEntityDto? {
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

    fun cancelBubble() {
        _uiState.update { state ->
            state.copy(
                showFunctionalBubble = false,
                pendingAction = null,
                pendingDto = null
            )
        }
    }
}