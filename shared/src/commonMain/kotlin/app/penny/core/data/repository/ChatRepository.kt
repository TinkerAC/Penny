package app.penny.core.data.repository

import app.penny.core.domain.model.ChatMessage
import app.penny.servershared.dto.requestDto.GetAiReplyResponse
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ChatRepository {
    suspend fun sendMessage(message: String): GetAiReplyResponse

    suspend fun sendAudio(audioFilePath: String): GetAiReplyResponse

    @OptIn(ExperimentalUuidApi::class)
    suspend fun findChatHistoryByUserUuid(
        userUuid: Uuid
    ): List<ChatMessage>

    suspend fun insert(chatMessage: ChatMessage)


    suspend fun update(chatMessage: ChatMessage)
}