// file: shared/src/commonMain/kotlin/app/penny/servershared/enumerate/Actions.kt
package app.penny.servershared.enumerate

import app.penny.servershared.dto.BaseEntityDto
import kotlinx.serialization.Serializable

@Serializable
sealed class Action {
    abstract val description: String
    abstract val actionName: String
    abstract val dto:BaseEntityDto?

    @Serializable
    data class InsertLedger(
        override val description: String = "Add a Ledger record",
        override val actionName: String = "insertLedgerRecord",
        override val dto: BaseEntityDto?
    ) : Action()

    @Serializable
    data class InsertTransaction(
        override val description: String = "Add a Transaction record",
        override val actionName: String = "insertTransactionRecord",
        override val dto: BaseEntityDto?
    ) : Action()
}