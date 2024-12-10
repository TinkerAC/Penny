// file: shared/src/commonMain/kotlin/app/penny/servershared/dto/ActionCompletable.kt
package app.penny.servershared.dto

import app.penny.servershared.enumerate.Action

/**
 * 接口用于判断实体是否完成特定的动作。
 */
interface ActionCompletable {
    /**
     * 判断实体是否完成指定的动作。
     * @param action 要执行的动作。
     * @return 如果实体完成该动作，返回 true，否则返回 false。
     */
    fun isCompleteFor(action: Action): Boolean
}
