package app.penny.core.domain.usecase

import app.penny.core.data.enumerate.MessageType
import app.penny.core.data.repository.ChatRepository
import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.model.UserMessage
import app.penny.core.domain.model.UserModel
import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SendMessageUseCase(
    private val chatRepository: ChatRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(
        messageText: String,
        userModel: UserModel
    ): Result<Pair<UserMessage, SystemMessage?>> {
        return try {
            // 构建 UserMessage
            val userMessage = UserMessage(
                content = messageText,
                type = MessageType.TEXT,
                uuid = Uuid.random(),
                timestamp = Clock.System.now().epochSeconds,
                user = userModel,
                sender = userModel
            )
            // 插入数据库
            chatRepository.insert(userMessage)

            // 调用 Repository 进行 AI 回复
            val aiReply = chatRepository.sendMessage(messageText)
            val aiMessage = SystemMessage(
                user = userModel,
                type = MessageType.TEXT,
                uuid = Uuid.random(),
                timestamp = Clock.System.now().epochSeconds,
                sender = UserModel.System,
                userIntent = aiReply.userIntent,
                content = aiReply.content
            )
            chatRepository.insert(aiMessage)

            // 返回用户消息和 AI 消息
            Result.success(userMessage to aiMessage)
        } catch (e: Exception) {
            Logger.e("Failed to send message", e)
            Result.failure(e)
        }
    }
}