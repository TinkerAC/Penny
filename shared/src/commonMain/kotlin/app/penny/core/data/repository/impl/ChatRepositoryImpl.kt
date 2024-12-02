package app.penny.core.data.repository.impl

import app.penny.core.data.database.ChatMessageLocalDataSource
import app.penny.core.data.model.toEntity
import app.penny.core.data.model.toModel
import app.penny.core.data.repository.ChatRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.UserModel
import app.penny.core.network.ApiClient
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
class ChatRepositoryImpl(
    private val chatMessageLocalDataSource: ChatMessageLocalDataSource,
    private val userRepository: UserRepository,
    private val apiClient: ApiClient
) : ChatRepository {
    override suspend fun sendMessage(message: String): ChatMessage.TextMessage {
        //TODO: Implement this with apiClient

        val action = apiClient.ai.getAction(message)

        return ChatMessage.TextMessage(
            uuid = Uuid.random(),
            user = userRepository.findAll().first(),
            sender = UserModel(
                uuid = Uuid.fromLongs(0, 0),
                username = "AI",
                email = ""
            ),
            content = "User wants to $action",
            timestamp = Clock.System.now().epochSeconds
        )
    }

    override suspend fun sendAudio(audioFilePath: String, duration: Long) {
        TODO("Not yet implemented")
    }

    @ExperimentalUuidApi
    override suspend fun findChatHistoryByUserUuid(userUuid: Uuid): List<ChatMessage> {
        return chatMessageLocalDataSource.findByUserUuid(userUuid.toString()).map { it.toModel() }
    }

    override suspend fun saveChatMessage(chatMessage: ChatMessage) {
        chatMessageLocalDataSource.insert(chatMessage.toEntity())
    }
}