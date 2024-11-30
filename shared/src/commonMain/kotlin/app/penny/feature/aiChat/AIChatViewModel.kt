// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/AIChatViewModel.kt
package app.penny.feature.aiChat

import app.penny.core.domain.model.UserModel
import app.penny.core.data.repository.ChatRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.model.ChatMessage
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AIChatViewModel(
    private val chatRepository: ChatRepository,
    private val userDataRepository: UserDataRepository,
    private val userRepository: UserRepository
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
                content = message,
                timestamp = Clock.System.now().epochSeconds

            )
            chatRepository.saveChatMessage(chatMessage)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + chatMessage,
                inputText = "",
                isSending = false
            )
            // Simulate AI assistant reply
            val aiReply = chatRepository.sendMessage(
                message = message
            )
            chatRepository.saveChatMessage(aiReply)

            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + aiReply
            )
        }
    }

    private fun sendAudio(audioFilePath: String, duration: Long) {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true)
            val audioMessage = ChatMessage.AudioMessage(
                uuid = Uuid.random(),
                user = currentUser,
                sender = currentUser,
                audioFilePath = audioFilePath,
                duration = duration,
                timestamp = Clock.System.now().epochSeconds
            )
            chatRepository.saveChatMessage(audioMessage)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + audioMessage,
                isSending = false
            )
            // Simulate AI assistant reply (optional)
        }
    }
}