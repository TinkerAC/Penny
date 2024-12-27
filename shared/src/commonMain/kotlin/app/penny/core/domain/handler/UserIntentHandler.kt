// file: shared/src/commonMain/kotlin/app/penny/core/domain/handler/UserIntentHandler.kt
package app.penny.core.domain.handler

import app.penny.core.domain.model.SystemMessage
import app.penny.servershared.dto.BaseEntityDto

/**
 * 动作处理器接口，用于处理特定类型的动作。
 */
interface UserIntentHandler {
    /**
     * 处理指定的动作和对应的Dto。
     * @param message 要处理的动作。
     * @param dto 与动作相关的Dto。
     */
    suspend fun handle(message: SystemMessage, dto: BaseEntityDto? = null): SystemMessage


}






