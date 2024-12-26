package app.penny.core.domain.usecase

import app.penny.core.domain.handler.ActionHandler
import app.penny.core.domain.model.SystemMessage
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.UserIntent
import app.penny.servershared.enumerate.UserIntentStatus
import co.touchlab.kermit.Logger
import kotlin.uuid.ExperimentalUuidApi

class ConfirmPendingActionUseCase(
    private val rebuildDtoUseCase: RebuildDtoUseCase,
    private val userIntentHandlers: Map<String, ActionHandler>
) {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(
        message: SystemMessage,
        editedFields: Map<String, String?>
    ): Result<SystemMessage> {
        val userIntent = message.userIntent ?: return Result.failure(
            IllegalStateException("No userIntent found in message")
        )

        val originalDto = (userIntent as? app.penny.servershared.enumerate.DtoAssociated)?.dto
        if (originalDto == null) {
            Logger.e("Original DTO is null for userIntent: ${userIntent.intentName}")
            return Result.failure(IllegalStateException("Original DTO is null"))
        }

        // 重新构建 DTO
        val newDto = rebuildDtoUseCase.execute(
            user = message.user,
            userIntent = userIntent,
            originalDto = originalDto,
            editedFields = editedFields
        ) ?: return Result.failure(IllegalStateException("Failed to rebuild DTO"))

        // 检查新的 DTO 是否完整
        if (!newDto.isCompleteFor(userIntent)) {
            Logger.e("DTO is still incomplete after editing for userIntent: ${userIntent.intentName}")
            // 返回标记为 Pending
            val incompleteMessage = message.copy(
                userIntent = userIntent.copy(
                    dto = newDto,
                    status = UserIntentStatus.Pending
                )
            )
            return Result.success(incompleteMessage)
        }

        // 如果 DTO 完整，尝试执行用户意图
        return try {
            executeUserIntent(message, userIntent, newDto)
        } catch (e: Exception) {
            Logger.e("Failed to execute userIntent: ${userIntent.intentName}", e)
            val failureMessage = message.copy(
                content = "执行操作失败: ${userIntent.intentName}",
                userIntent = userIntent.copy(status = UserIntentStatus.Failed)
            )
            Result.failure(
                Exception("Failed to execute user intent", e)
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun executeUserIntent(
        message: SystemMessage,
        userIntent: UserIntent,
        dto: BaseEntityDto
    ): Result<SystemMessage> {
        val handler = userIntentHandlers[userIntent::class.simpleName]
            ?: return Result.failure(Exception("No handler found for userIntent: ${userIntent.intentName}"))

        return try {
            handler.handle(userIntent, dto)
            val successMessage = "成功执行操作: ${userIntent.intentName}"
            val updatedMessage = message.copy(
                content = successMessage,
                userIntent = userIntent.copy(status = UserIntentStatus.Completed)
            )
            Result.success(updatedMessage)
        } catch (e: Exception) {
            Logger.e("Failed to execute userIntent: ${userIntent.intentName}", e)
            val failureMessage = message.copy(
                content = "执行操作失败: ${userIntent.intentName}",
                userIntent = userIntent.copy(status = UserIntentStatus.Failed)
            )
            Result.failure(Exception("Failed to execute user intent", e))
        }
    }
}