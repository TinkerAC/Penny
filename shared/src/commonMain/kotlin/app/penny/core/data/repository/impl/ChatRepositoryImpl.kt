package app.penny.core.data.repository.impl

import app.penny.core.data.database.ChatMessageLocalDataSource
import app.penny.core.data.model.toEntity
import app.penny.core.data.model.toModel
import app.penny.core.data.repository.ChatRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.UserModel
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.requestDto.GetAiReplyResponse
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
class ChatRepositoryImpl(
    private val chatMessageLocalDataSource: ChatMessageLocalDataSource,
    private val userRepository: UserRepository,
    private val apiClient: ApiClient
) : ChatRepository {
    override suspend fun sendMessage(message: String): GetAiReplyResponse {
        //TODO: Implement this with apiClient

        val response = apiClient.ai.getAction(message)

        return response


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