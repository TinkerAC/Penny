// file: shared/src/commonMain/kotlin/app/penny/servershared/enumerate/Actions.kt
package app.penny.servershared.enumerate

import kotlinx.serialization.Serializable

@Serializable
sealed class Action {
    abstract val description: String
    abstract val actionName: String
    abstract val actionDetail: String?

    @Serializable
    data class InsertLedger(
        override val description: String = "Add a Ledger record",
        override val actionName: String = "insertLedgerRecord",
        override val actionDetail: String?
    ) : Action()

    @Serializable
    data class InsertTransaction(
        override val description: String = "Add a Transaction record",
        override val actionName: String = "insertTransactionRecord",
        override val actionDetail: String?
    ) : Action()
}