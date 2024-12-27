package app.penny.core.domain.usecase

import app.penny.core.domain.handler.UserIntentHandlers
import app.penny.core.domain.model.SystemMessage
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.UserIntent
import app.penny.servershared.enumerate.UserIntentStatus
import co.touchlab.kermit.Logger
import kotlin.uuid.ExperimentalUuidApi

class ConfirmPendingActionUseCase(
    private val rebuildDtoUseCase: RebuildDtoUseCase,
    private val userIntentHandlers: UserIntentHandlers
) {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(
        message: SystemMessage, editedFields: Map<String, String?>
    ): Result<SystemMessage> {
        val userIntent = message.userIntent

        val originalDto = (userIntent as? app.penny.servershared.enumerate.DtoAssociated)?.dto
        if (originalDto == null) {
            Logger.e("Original DTO is null for userIntent: ${userIntent.displayText}")
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
            Logger.e("DTO is still incomplete after editing for userIntent: ${userIntent.displayText}")
            // 返回标记为 Pending
            val incompleteMessage = message.copy(
                userIntent = userIntent.copy(
                    dto = newDto, status = UserIntentStatus.Pending
                )
            )
            return Result.success(incompleteMessage)
        }

        // 如果 DTO 完整，尝试执行用户意图
        return try {

            executeUserIntent(message, userIntent, newDto)

        } catch (e: Exception) {

            Logger.e("Failed to execute userIntent: ${userIntent.displayText}", e)
            val failureMessage = message.copy(
                content = "执行操作失败: ${userIntent.displayText}",
                userIntent = userIntent.copy(status = UserIntentStatus.Failed)
            )
            Result.failure(
                Exception("Failed to execute user intent", e)
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun executeUserIntent(
        message: SystemMessage, userIntent: UserIntent, dto: BaseEntityDto
    ): Result<SystemMessage> {
        return try {
            val handledMessage = userIntentHandlers.handle(message)
            Result.success(handledMessage)
        } catch (e: Exception) {
            Logger.e("Failed to execute userIntent: ${userIntent.displayText}", e)
            val failureMessage = message.copy(
                content = "执行操作失败: ${userIntent.displayText}",
                userIntent = userIntent.copy(status = UserIntentStatus.Failed)
            )
            Result.failure(Exception("Failed to execute user intent", e))
        }
    }
}