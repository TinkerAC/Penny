// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/AIChatViewModel.kt
package app.penny.feature.aiChat

import app.penny.core.data.model.toModel
import app.penny.core.domain.model.UserModel
import app.penny.core.data.repository.ChatRepository
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.enum.Currency
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.LedgerModel
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.dto.entityDto.LedgerDto
import app.penny.servershared.enumerate.Action
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
class AIChatViewModel(
    private val chatRepository: ChatRepository,
    private val userDataRepository: UserDataRepository,
    private val userRepository: UserRepository,
    private val ledgerRepository: LedgerRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(AIChatUiState())
    val uiState: StateFlow<AIChatUiState> = _uiState.asStateFlow()

    private lateinit var currentUser: UserModel

    init {
        screenModelScope.launch {
            currentUser = userRepository.findByUuid(
                userDataRepository.getUserUuid()
            )!!
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
            _uiState.value = _uiState.value.copy(isLoading = true)
            val messages = chatRepository.findChatHistoryByUserUuid(currentUser.uuid)
            _uiState.value = _uiState.value.copy(messages = messages, isLoading = false)
        }
    }

    private fun sendMessage(message: String) {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true)
            val chatMessage = ChatMessage.TextMessage(
                uuid = Uuid.random(),
                user = currentUser,
                sender = currentUser,
                timestamp = Clock.System.now().epochSeconds,
                content = message
            )
            chatRepository.saveChatMessage(chatMessage)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + chatMessage,
                inputText = "",
                isSending = false
            )

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

            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + cm
            )
        }
    }

    private fun sendAudio(audioFilePath: String, duration: Long) {
        throw NotImplementedError("Audio messages are not supported yet")
    }

    private fun handleAction(action: Action) {


        screenModelScope.launch {

            val dto = action.dto

            if (dto == null || dto.completedForAction()) {

                executeAction(action, dto!!)
            } else {
                _uiState.value = _uiState.value.copy(
                    showFunctionalBubble = true,
                    pendingAction = action,
                    pendingDto = dto
                )
            }

        }
    }


    private suspend fun executeAction(action: Action, dto: BaseEntityDto) {
        when (action) {
            is Action.InsertLedger -> {
                val ledgerDto = dto as LedgerDto
                val ledger = ledgerRepository.insert(
                    ledgerDto.toModel()
                )
                val chatMessage = ChatMessage.TextMessage(
                    uuid = Uuid.random(),
                    user = currentUser,
                    sender = UserModel.AI,
                    timestamp = Clock.System.now().epochSeconds,
                    content = "Successfully created ledger"
                )
                chatRepository.saveChatMessage(chatMessage)
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + chatMessage,
                    showFunctionalBubble = false,
                    pendingAction = null,
                    pendingDto = null
                )
            }

            else -> {
                throw IllegalArgumentException("Unsupported action: $action")
            }
        }
    }

    private fun confirmPendingAction(action: Action, editedFields: Map<String, String?>) {
        screenModelScope.launch {
            val originalDto = _uiState.value.pendingDto
            val newDto = rebuildDto(action, originalDto, editedFields)
            if (newDto != null && newDto.completedForAction()) {
                executeAction(action, newDto)
            } else {
                // 用户编辑后仍不完整，继续显示bubble或报错，这里简单处理继续显示
                _uiState.value = _uiState.value.copy(
                    showFunctionalBubble = true,
                    pendingAction = action,
                    pendingDto = newDto ?: originalDto
                )
            }
        }
    }

    private fun rebuildDto(
        action: Action,
        originalDto: BaseEntityDto?,
        editedFields: Map<String, String?>
    ): BaseEntityDto? {
        // 根据action类型重建dto
        return when (action.actionName) {
            "insertLedgerRecord" -> {
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


