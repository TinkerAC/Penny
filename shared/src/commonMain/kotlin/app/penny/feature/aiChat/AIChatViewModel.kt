package app.penny.feature.aiChat

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.usecase.ConfirmPendingActionUseCase
import app.penny.core.domain.usecase.LoadChatHistoryUseCase
import app.penny.core.domain.usecase.SendMessageUseCase
import app.penny.servershared.enumerate.DtoAssociated
import app.penny.servershared.enumerate.UserIntentStatus
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class AIChatViewModel(
    private val loadChatHistoryUseCase: LoadChatHistoryUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val confirmPendingActionUseCase: ConfirmPendingActionUseCase,
    private val userDataRepository: UserDataRepository,
    private val ledgerRepository: LedgerRepository


) : ScreenModel {

    private val _uiState = MutableStateFlow(AIChatUiState())
    val uiState: StateFlow<AIChatUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            // 初始化用户 & 默认账本
            val user = userDataRepository.getUser()
            val defaultLedger = userDataRepository.getDefaultLedger()
            _uiState.update {
                it.copy(
                    user = user,
                    ledgerList = ledgerRepository.findByUserUuid(user.uuid),
                    selectedLedger = defaultLedger
                )
            }
            // 立即加载聊天记录
            loadChatHistory()
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
            AIChatIntent.HideLedgerSelectDialog -> {
                _uiState.update { it.copy(ledgerSelectDialogVisible = false) }
            }

            is AIChatIntent.SelectLedger -> {
                _uiState.update { it.copy(selectedLedger = intent.ledger) }
            }

            AIChatIntent.ShowLedgerSelectDialog -> {
                _uiState.update { it.copy(ledgerSelectDialogVisible = true) }
            }
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun loadChatHistory() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userUuid = _uiState.value.user.uuid
            val result = loadChatHistoryUseCase.execute(userUuid)
            result.onSuccess { messages ->
                _uiState.update {
                    it.copy(messages = messages, isLoading = false)
                }
            }.onFailure { e ->
                Logger.e("Failed to load chat history", e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun sendMessage(messageText: String) {
        screenModelScope.launch {
            _uiState.update { it.copy(isSending = true) }
            val userModel = _uiState.value.user
            val result = sendMessageUseCase.execute(messageText, userModel)

            result.onSuccess { (userMsg, aiMsg) ->
                // 更新UIState里的消息列表
                _uiState.update { state ->
                    val newMessages = buildList {
                        addAll(state.messages)
                        add(userMsg)
                        if (aiMsg != null) add(aiMsg)
                    }
                    state.copy(messages = newMessages, isSending = false, inputText = "")
                }
            }.onFailure { e ->
                Logger.e("Failed to send message", e)
                _uiState.update { it.copy(isSending = false) }
            }
        }
    }

    private fun confirmPendingAction(
        message: SystemMessage,
        editedFields: Map<String, String?>
    ) {
        val userIntent = message.userIntent
        if (userIntent !is DtoAssociated) {
            Logger.e("UserIntent does not implement DtoAssociated")
            return
        }

        screenModelScope.launch {
            val result = confirmPendingActionUseCase.execute(message, editedFields)
            result.onSuccess { updatedMessage ->
                // 根据返回的 updatedMessage 更新 UIState
                updateMessage(updatedMessage)
            }.onFailure { e ->
                Logger.e("Failed to confirm pending action", e)
                // 这里你也可以再更新一个失败提示消息
            }
        }
    }

    private fun dismissFunctionalMessage(message: SystemMessage) {
        screenModelScope.launch {
            message.userIntent?.let { it.status = UserIntentStatus.Cancelled }
            updateMessage(message)
        }
    }

    private fun sendAudio(audioFilePath: String, duration: Long) {
        // 目前没有实现，就先留在 ViewModel
        throw NotImplementedError("Audio messages are not supported yet")
    }


    @OptIn(ExperimentalUuidApi::class)
    private fun updateMessage(updatedMessage: app.penny.core.domain.model.ChatMessage) {
        _uiState.update {
            it.copy(messages = it.messages.map { msg ->
                if (msg.uuid == updatedMessage.uuid) updatedMessage else msg
            })
        }
    }
}