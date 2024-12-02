// 文件：server/src/main/kotlin/app/penny/services/AiService.kt
package app.penny.services

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

class AiService(
    private val openAiClient: OpenAI
) {


    suspend fun getAction(
        text: String
    ): String? {
        val prompt =
            """I am a financial assistant. The user will input natural language to describe accounting tasks. Your task is to identify the user's intent and return the corresponding method name.
            Supported methods:
            - insertLedgerRecord (add a Ledger record)
            - insertTransactionRecord (add a Transaction record)
            - queryRecords (retrieve financial records)
            - generateReport (create a financial report)
            - exportRecords (export financial records)
            - updateRecord (update an existing financial record)
            - deleteRecord (delete a financial record)
            - setBudget (define a budget)
            - queryBudget (retrieve the budget information)
            - analyzeSpending (analyze spending habits)
            - setReminder (set financial reminders)
            - clearData (remove all financial records)

            Examples:
            - "I spent $50 at a supermarket today" => insertRecord
            - "Show me the spending records for November" => queryRecords
            - "Export my financial records from last month" => exportRecords
            """


        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("text-davinci-003"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = prompt
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = text
                )
            )
        )
        val completion: ChatCompletion = openAiClient.chatCompletion(chatCompletionRequest)

        return completion.choices.first().message.content
    }


}