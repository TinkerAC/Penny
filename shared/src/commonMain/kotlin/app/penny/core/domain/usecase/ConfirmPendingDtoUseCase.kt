package app.penny.core.domain.usecase

import app.penny.core.domain.handler.UserIntentHandlers
import app.penny.core.domain.model.SystemMessage
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.DtoAssociated
import app.penny.servershared.enumerate.UserIntent
import app.penny.servershared.enumerate.UserIntentStatus
import co.touchlab.kermit.Logger
import kotlin.uuid.ExperimentalUuidApi

/**
 * 当用户在 EditSheet 上完成编辑后（产出一个完整的 DTO），调用此 UseCase 进行下一步逻辑：
 *   1. 检查 DTO 是否完整（isCompleteFor）。
 *   2. 若不完整，则维持 Pending 状态并提示用户继续补充。
 *   3. 若完整，则执行 userIntentHandlers.handle(message) 来真正执行此 UserIntent。
 */
class ConfirmPendingActionUseCase(
    private val userIntentHandlers: UserIntentHandlers
) {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(
        // 原始 SystemMessage
        message: SystemMessage,
    ): Result<SystemMessage> {
        val userIntent = message.userIntent

        // 只有实现了 DtoAssociated 的 Intent 才需要在此进行 DTO 完整度校验
        if (userIntent is DtoAssociated) {

            val updatedDto = userIntent.dto

            // 如果编辑后的 DTO 为空，无法继续
            if (updatedDto == null) {
                Logger.e("Updated DTO is null for userIntent: ${userIntent.displayText}")
                return Result.failure(
                    IllegalStateException("Updated DTO is null, cannot proceed.")
                )
            }

            // 检查新的 DTO 是否满足操作要求
            if (!updatedDto.isCompleteFor(userIntent)) {
                Logger.e("DTO is still incomplete after editing for userIntent: ${userIntent.displayText}")

                // 将状态仍设为 Pending 并提示用户继续编辑
                val incompleteMessage = message.copy(
                    userIntent = userIntent.copy(
                        dto = updatedDto, status = UserIntentStatus.Pending
                    ), executeLog = "请继续编辑以完成操作: ${userIntent.displayText}"
                )
                return Result.success(incompleteMessage)
            }

            // 如果 DTO 完整，则尝试执行用户意图
            return try {
                executeUserIntent(message, userIntent, updatedDto)
            } catch (e: Exception) {
                Logger.e("Failed to execute userIntent: ${userIntent.displayText}", e)
                val failureMessage = message.copy(
                    content = "执行操作失败: ${userIntent.displayText}",
                    userIntent = userIntent.copy(status = UserIntentStatus.Failed),
                    executeLog = "Failed to execute userIntent: ${userIntent.displayText}"
                )
                Result.failure(Exception("Failed to execute user intent", e))
            }

        } else {
            // 如果 userIntent 并不需要绑定某个 DTO，则直接交给 handler
            val handledMessage = userIntentHandlers.handle(message)

            return Result.success(handledMessage)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun executeUserIntent(
        message: SystemMessage, userIntent: UserIntent, dto: BaseEntityDto
    ): Result<SystemMessage> {
        return try {
            // 将更新后的 message + dto 交由 handler 处理
            val handledMessage = userIntentHandlers.handle(
                message.copy(
                    userIntent = userIntent.copy(dto = dto)
                )
            )
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