// file: shared/src/commonMain/kotlin/app/penny/core/domain/handler/ActionHandler.kt
package app.penny.core.domain.handler

import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.Action

/**
 * 动作处理器接口，用于处理特定类型的动作。
 */
interface ActionHandler {
    /**
     * 处理指定的动作和对应的Dto。
     * @param action 要处理的动作。
     * @param dto 与动作相关的Dto。
     */
    suspend fun handle(action: Action, dto: BaseEntityDto)
}
