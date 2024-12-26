// file: shared/src/commonMain/kotlin/app/penny/servershared/enumerate/UserIntent.kt
package app.penny.servershared.enumerate

import app.penny.servershared.dto.BaseEntityDto
import kotlinx.serialization.Serializable

enum class UserIntentStatus {
    Pending, Completed, Cancelled, Failed
}

interface DtoAssociated {
    val dto: BaseEntityDto?
}

interface RequireConfirmation {
    val requireConfirmation: Boolean
}


@Serializable
sealed class UserIntent : RequireConfirmation {
    abstract val description: String
    abstract val intentName: String
    abstract var status: UserIntentStatus

    abstract fun copy(
        dto: BaseEntityDto? = null, status: UserIntentStatus = this.status
    ): UserIntent

    @Serializable
    data class InsertLedger(
        override val description: String = "Add a Ledger record",
        override val intentName: String = "insertLedgerRecord",
        override val dto: BaseEntityDto?,
        override var status: UserIntentStatus = UserIntentStatus.Pending,
        override val requireConfirmation: Boolean = true
    ) : UserIntent(), DtoAssociated {
        override fun copy(
            dto: BaseEntityDto?, status: UserIntentStatus
        ): InsertLedger {
            return InsertLedger(description, intentName, dto, status)
        }
    }

    @Serializable
    data class InsertTransaction(
        override val description: String = "Add a Transaction record",
        override val intentName: String = "insertTransactionRecord",
        override var status: UserIntentStatus = UserIntentStatus.Pending,
        override val dto: BaseEntityDto?,
        override val requireConfirmation: Boolean = true
    ) : UserIntent(), DtoAssociated {
        override fun copy(dto: BaseEntityDto?, status: UserIntentStatus): InsertTransaction {
            return InsertTransaction(description, intentName, status, dto)
        }
    }


    @Serializable
    data class JustTalk(
        override val description: String = "Just talk",
        override val intentName: String = "justTalk",
        override var status: UserIntentStatus = UserIntentStatus.Completed,
        val aiReplyText: String?,
        override val requireConfirmation: Boolean = false,
    ) : UserIntent() {
        override fun copy(dto: BaseEntityDto?, status: UserIntentStatus): JustTalk {
            return JustTalk(description, intentName, status, aiReplyText)
        }
    }

    @Serializable
    data class SyncData(
        override val description: String = "Sync data",
        override val intentName: String = "syncData",
        override var status: UserIntentStatus = UserIntentStatus.Pending,
        override val requireConfirmation: Boolean = false
    ) : UserIntent() {
        override fun copy(dto: BaseEntityDto?, status: UserIntentStatus): UserIntent {
            return SyncData(description, intentName, status)
        }

    }


}