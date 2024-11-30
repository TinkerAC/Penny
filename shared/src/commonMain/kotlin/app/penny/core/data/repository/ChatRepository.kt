package app.penny.core.data.repository

import app.penny.core.domain.model.ChatMessage
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ChatRepository {
    suspend fun sendMessage(message: String): ChatMessage.TextMessage

    suspend fun sendAudio(audioFilePath: String, duration: Long)
    @OptIn(ExperimentalUuidApi::class)
    suspend fun findChatHistoryByUserUuid(
        userUuid: Uuid

    ):List<ChatMessage>

    suspend fun saveChatMessage(chatMessage: ChatMessage)
}