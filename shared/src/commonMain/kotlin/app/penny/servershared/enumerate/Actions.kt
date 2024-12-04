package app.penny.servershared.enumerate

import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.dto.entityDto.LedgerDto
import app.penny.servershared.dto.entityDto.TransactionDto
import kotlinx.serialization.Serializable

@Serializable
sealed class Action(
) {
    // 添加一个字段表示操作的名称，方便动态生成提示
    abstract val description: String
    abstract val actionName: String
    abstract val dto: BaseEntityDto?


    // 插入账本记录
    @Serializable
    data class InsertLedger(
        override val description: String = "Add a Ledger record",
        override val actionName: String = "insertLedgerRecord",
        override val dto: LedgerDto?
    ) : Action() {

    }

    // 插入交易记录
    @Serializable
    data class InsertTransaction(
        override val description: String = "Add a Transaction record",
        override val actionName: String = "insertTransactionRecord",
        override val dto: TransactionDto?
    ) : Action() {

    }


}
