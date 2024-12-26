package app.penny.core.domain.usecase

import app.penny.core.data.repository.ChatRepository
import app.penny.core.domain.model.ChatMessage
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class LoadChatHistoryUseCase(
    private val chatRepository: ChatRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(userUuid: Uuid): Result<List<ChatMessage>> {
        return try {
            val messages = chatRepository.findChatHistoryByUserUuid(userUuid)
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}