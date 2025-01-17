package app.penny.core.data.repository.impl

import app.penny.core.data.database.ChatMessageLocalDataSource
import app.penny.core.data.enumerate.toEntity
import app.penny.core.data.enumerate.toModel
import app.penny.core.data.repository.ChatRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.model.ChatMessage
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.requestDto.GetAiReplyResponse
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
class ChatRepositoryImpl(
    private val chatMessageLocalDataSource: ChatMessageLocalDataSource,
    private val userRepository: UserRepository,
    private val apiClient: ApiClient
) : ChatRepository {
    override suspend fun sendMessage(message: String): GetAiReplyResponse {

        val response = apiClient.ai.getAiReply(message)

        return response


    }

    override suspend fun sendAudio(audioFilePath: String): GetAiReplyResponse {
        return apiClient.ai.getAiReplyAudio(audioFilePath)
    }

    @ExperimentalUuidApi
    override suspend fun findChatHistoryByUserUuid(userUuid: Uuid): List<ChatMessage> {
        return chatMessageLocalDataSource.findByUserUuid(userUuid.toString()).map { it.toModel() }
    }

    override suspend fun insert(chatMessage: ChatMessage) {
        chatMessageLocalDataSource.insert(chatMessage.toEntity())
    }

    override suspend fun update(chatMessage: ChatMessage) {
        chatMessageLocalDataSource.updateByUuid(chatMessage.toEntity())
    }
}