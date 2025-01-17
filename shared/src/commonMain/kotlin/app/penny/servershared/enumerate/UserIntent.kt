// file: shared/src/commonMain/kotlin/app/penny/servershared/enumerate/UserIntent.kt
package app.penny.servershared.enumerate

import app.penny.servershared.dto.BaseEntityDto
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

enum class UserIntentStatus {
    Pending, Completed, Cancelled, Failed
}

interface DtoAssociated {
    val dto: BaseEntityDto?
}

/**
 * Whether the user intent requires confirmation by the user.
 */
interface ConfirmRequired


/**
 * A salient intent is an intent that will not show on the UI,example [UserIntent.JustTalk]
 */
interface SilentIntent {
}

@Serializable
sealed class UserIntent {
    abstract val displayText: StringResource
    abstract val description: String
    abstract var status: UserIntentStatus
    abstract val example: String
    abstract val completeDescription: StringResource
    abstract fun copy(
        dto: BaseEntityDto? = null, status: UserIntentStatus = this.status
    ): UserIntent

    @Serializable
    data class InsertLedger(
        @Transient
        override val displayText: StringResource = SharedRes.strings.user_intent_insert_ledger,
        @Transient
        override val completeDescription: StringResource = SharedRes.strings.user_intent_insert_ledger_complete_description,
        override val description: String = "Add a Ledger record",
        override val example: String = "Create a new ledger called 'Expenses' in USD => InsertLedgerRecord",
        override val dto: BaseEntityDto? = null,
        override var status: UserIntentStatus = UserIntentStatus.Pending,
    ) : UserIntent(), DtoAssociated, ConfirmRequired {
        override fun copy(
            dto: BaseEntityDto?, status: UserIntentStatus
        ): InsertLedger {
            return InsertLedger(
                description = this.description,
                example = this.example,
                dto = dto ?: this.dto,
                status = status,
            )
        }
    }

    @Serializable
    data class InsertTransaction(
        @Transient
        override val displayText: StringResource = SharedRes.strings.user_intent_insert_transaction,
        @Transient
        override val completeDescription: StringResource = SharedRes.strings.user_intent_insert_transaction_complete_description,
        override val description: String = "Add a Transaction record",
        override val example: String = "I spent \$50 at a supermarket today => InsertTransactionRecord",
        override val dto: BaseEntityDto? = null,
        override var status: UserIntentStatus = UserIntentStatus.Pending,
    ) : UserIntent(), DtoAssociated, ConfirmRequired {
        override fun copy(dto: BaseEntityDto?, status: UserIntentStatus): InsertTransaction {
            return InsertTransaction(
                description = this.description,
                example = this.example,
                dto = dto ?: this.dto,
                status = status,
            )
        }
    }

    @Serializable
    data class JustTalk(
        @Transient
        override val displayText: StringResource = SharedRes.strings.user_intent_just_talk, // should not appear in the UI
        @Transient
        override val completeDescription: StringResource = SharedRes.strings.user_intent_just_talk_complete_description,
        override val description: String = "Just talk",
        override val example: String = "How is the weather today? => JustTalk",
        override var status: UserIntentStatus = UserIntentStatus.Completed,
        val aiReplyText: String? = null,
    ) : UserIntent(), SilentIntent {
        override fun copy(dto: BaseEntityDto?, status: UserIntentStatus): JustTalk {
            return JustTalk(
                description = this.description,
                example = this.example,
                status = status,
                aiReplyText = this.aiReplyText,
            )
        }
    }

    @Serializable
    data class SyncData(
        @Transient
        override val displayText: StringResource = SharedRes.strings.user_intent_sync_data,
        @Transient
        override val completeDescription: StringResource = SharedRes.strings.user_intent_sync_data_complete_description,
        override val description: String = "Sync data with the server",
        override val example: String = "Sync my data with the server => SyncData",
        override var status: UserIntentStatus = UserIntentStatus.Pending,
    ) : UserIntent(), SilentIntent {
        override fun copy(dto: BaseEntityDto?, status: UserIntentStatus): SyncData {
            return SyncData(
                description = this.description,
                example = this.example,
                status = status,
            )
        }
    }


    @Serializable
    data class GenerateMonthlyReport(
        @Transient
        override val displayText: StringResource = SharedRes.strings.user_intent_generate_monthly_report,
        @Transient
        override val completeDescription: StringResource = SharedRes.strings.user_intent_generate_monthly_report_complete_description,
        override val description: String = "Generate a financial report of a specific month",
        override val example: String = "Generate a report for the month of November => generateReport",
        override var status: UserIntentStatus = UserIntentStatus.Pending,
        val month: Int? = null,
        val year: Int? = null,

        ) : UserIntent(),SilentIntent {
        override fun copy(dto: BaseEntityDto?, status: UserIntentStatus): GenerateMonthlyReport {
            return GenerateMonthlyReport(
                description = this.description,
                example = this.example,
                status = status,
            )
        }
    }


    companion object {
        private val allIntents = listOf(
            InsertLedger(),
            InsertTransaction(),
            JustTalk(),
            SyncData(),
            GenerateMonthlyReport(),
        )

        fun fromKClassSimpleName(simpleName: String): UserIntent? {
            return allIntents.find { it::class.simpleName == simpleName }
        }

        fun generatePrompt(): String {
            val basePrompt = """
                [Role]
                You are a helpful financial assistant.
                [Goal]
                The user will input natural language to describe accounting tasks.
                Your goal is to identify the user's intent and return the corresponding method name listed below.
                Supported methods:
            """.trimIndent()

            // 正确获取每个 UserIntent 的类名和描述
            val methodsList = allIntents.joinToString("\n") {
                "- ${it::class.simpleName}(\"${it.description}\")"
            }

            // 5 个随机示例
            val examples = allIntents.shuffled().take(5).joinToString("\n") {
                "- ${it.example}"
            }

            val fullPrompt = """
                $basePrompt
                $methodsList
                [Examples]
                $examples
            """.trimIndent()

            return fullPrompt
        }
    }
}
