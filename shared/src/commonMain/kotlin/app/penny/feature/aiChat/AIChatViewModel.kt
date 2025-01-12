package app.penny.feature.aiChat

import app.penny.core.data.enumerate.MessageType
import app.penny.core.data.repository.ChatRepository
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.handler.UserIntentHandlers
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.model.UserMessage
import app.penny.core.domain.model.UserModel
import app.penny.core.domain.usecase.ConfirmPendingActionUseCase
import app.penny.platform.AudioRecorderFactory
import app.penny.presentation.utils.generateGravatarUrl
import app.penny.servershared.enumerate.DtoAssociated
import app.penny.servershared.enumerate.SilentIntent
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
import me.sujanpoudel.utils.paths.appCacheDirectory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AIChatViewModel(
    private val confirmPendingActionUseCase: ConfirmPendingActionUseCase,
    private val userDataRepository: UserDataRepository,
    private val ledgerRepository: LedgerRepository,
    private val chatRepository: ChatRepository,
    private val userIntentHandlers: UserIntentHandlers

) : ScreenModel {

    private val _uiState = MutableStateFlow(AIChatUiState())
    val uiState: StateFlow<AIChatUiState> = _uiState.asStateFlow()
    val recorder =
        AudioRecorderFactory.createAudioRecorder(appCacheDirectory("app.penny").toString())

    init {
        screenModelScope.launch {
            // 初始化用户 & 默认账本
            val user = userDataRepository.getUser()
            val defaultLedger = userDataRepository.getDefaultLedger()
            _uiState.update {
                it.copy(
                    user = user,
                    ledgerList = ledgerRepository.findByUserUuid(user.uuid),
                    selectedLedger = defaultLedger,
                    userAvatarUrl = user.email?.let { it1 -> generateGravatarUrl(it1) }
                )
            }
            // 立即加载聊天记录
            loadChatHistory()
        }
    }

    fun handleIntent(intent: AIChatIntent) {
        when (intent) {
            is AIChatIntent.SendTextMessage -> sendChatMessage(
                messageType = MessageType.TEXT,
                messageText = intent.message
            )

            is AIChatIntent.ConfirmPendingAction -> confirmPendingAction(
                intent.message
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

            AIChatIntent.ToggleInputMode -> {
                _uiState.update {
                    it.copy(
                        inputMode = if (it.inputMode == MessageType.TEXT) MessageType.AUDIO else MessageType.TEXT
                    )
                }
            }

            AIChatIntent.StartRecord -> startRecording()

            AIChatIntent.StopRecordAndDiscard -> {
                screenModelScope.launch {
                    stopRecording()
                }
            }

            AIChatIntent.StopRecordAndSend -> {
                screenModelScope.launch {
                    val path = stopRecording()
                    println("audio file path: $path")
                    sendChatMessage(
                        messageType = MessageType.AUDIO,
                        audioFilePath = path
                    )
                }

            }

        }
    }

    private suspend fun stopRecording(): String {
        val audioFilePath = recorder.stopRecording()
        println("recording stopped, file saved at $audioFilePath")

        _uiState.update { it.copy(isRecording = false) }
        return audioFilePath


    }


    private fun startRecording() {
        screenModelScope.launch {
            val audioFilePath = recorder.startRecording()
        }
        _uiState.update { it.copy(isRecording = true) }
    }


    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun loadChatHistory() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userUuid = _uiState.value.user.uuid
            val result = try {
                val messages = chatRepository.findChatHistoryByUserUuid(userUuid)
                Result.success(messages)
            } catch (e: Exception) {
                Result.failure(e)
            }
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

    private fun sendChatMessage(
        messageType: MessageType,
        messageText: String? = null,
        audioFilePath: String? = null
    ) {
        screenModelScope.launch {
            _uiState.update { it.copy(isSending = true) }
            val userModel = _uiState.value.user
            var userMessage: UserMessage? = null
            val result = try {
                when (messageType) {
                    MessageType.TEXT -> {
                        // 构建 UserMessage
                        userMessage = buildMessage(
                            messageType = MessageType.TEXT,
                            sender = userModel,
                            content = messageText
                        ) as UserMessage
                    }

                    MessageType.AUDIO -> {
                        // 构建 UserMessage
                        userMessage = buildMessage(
                            messageType = MessageType.AUDIO,
                            sender = userModel,
                            content = "Audio message"
                        ) as UserMessage
                    }
                }

                // 插入数据库 和UI
                chatRepository.insert(userMessage)
                addMessageToUiState(userMessage)

                // 调用 Repository 进行 AI 回复
                val aiReply = when (messageType) {
                    MessageType.TEXT -> chatRepository.sendMessage(messageText!!)
                    MessageType.AUDIO -> chatRepository.sendAudio(audioFilePath!!)
                    else -> throw IllegalStateException("Invalid message type")
                }

                val aiMessage = buildMessage(
                    messageType = MessageType.TEXT,
                    sender = UserModel.System,
                    content = aiReply.content,
                    userIntent = aiReply.userIntent
                ) as SystemMessage

                

                when (aiMessage.userIntent) {
                    is SilentIntent -> {
                        val handledMessage = userIntentHandlers.handle(aiMessage)
                        addMessageToUiState(handledMessage)
                        chatRepository.insert(handledMessage)
                    }

                    else -> {
                        chatRepository.insert(aiMessage)
                        addMessageToUiState(aiMessage)
                    }
                }

                Result.success(userMessage to aiMessage)
            } catch (e: Exception) {
                Logger.e("Failed to send message", e)
                Result.failure<Pair<UserMessage, SystemMessage?>>(e)
            }


            result.onSuccess { (userMsg, aiMsg) ->

            }.onFailure { e ->
                Logger.e("Failed to send message", e)
                _uiState.update { it.copy(isSending = false) }
            }
        }

    }


    private fun confirmPendingAction(
        message: SystemMessage,
    ) {
        val userIntent = message.userIntent
        if (userIntent !is DtoAssociated) {
            Logger.e("UserIntent does not implement DtoAssociated")
            return
        }
        screenModelScope.launch {
            val result = confirmPendingActionUseCase.execute(message)//todo
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
            message.userIntent.status = UserIntentStatus.Cancelled
            updateMessage(message)
        }
    }


    /**
     * Update message in database and UIState
     */
    @OptIn(ExperimentalUuidApi::class)
    private fun updateMessage(updatedMessage: ChatMessage) {

        //update message in database
        screenModelScope.launch {
            chatRepository.update(updatedMessage)
        }

        _uiState.update {
            it.copy(
                messages = it.messages.map { msg ->
                    if (msg.uuid == updatedMessage.uuid) updatedMessage else msg
                })
        }
    }

    private fun addMessageToUiState(message: ChatMessage) {
        _uiState.update {
            it.copy(
                messages = buildList {
                    addAll(_uiState.value.messages)
                    add(message)
                }
            )
        }
    }


    private fun modifyMessage(message: ChatMessage) {
        _uiState.update {
            it.copy(
                messages = it.messages.map { msg ->
                    if (msg.uuid == message.uuid) message else msg
                }
            )
        }
    }


    private fun buildMessage(
        messageType: MessageType,
        sender: UserModel,
        content: String? = null,
        userIntent: UserIntent? = null
    ): ChatMessage {
        if (sender != UserModel.System) {
            when (messageType) {
                MessageType.TEXT -> {
                    return UserMessage(
                        content = content,
                        type = MessageType.TEXT,
                        uuid = Uuid.random(),
                        timestamp = Clock.System.now().epochSeconds,
                        user = _uiState.value.user,
                        sender = sender
                    )
                }

                MessageType.AUDIO -> {
                    return UserMessage(
                        content = "Audio message",
                        type = MessageType.AUDIO,
                        uuid = Uuid.random(),
                        timestamp = Clock.System.now().epochSeconds,
                        user = _uiState.value.user,
                        sender = sender
                    )
                }
            }
        } else {
            return SystemMessage(
                content = content,
                type = MessageType.TEXT,
                uuid = Uuid.random(),
                timestamp = Clock.System.now().epochSeconds,
                sender = UserModel.System,
                user = _uiState.value.user,
                userIntent = userIntent ?: UserIntent.JustTalk()
            )
        }
    }

}



