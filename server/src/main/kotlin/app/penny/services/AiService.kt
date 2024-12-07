// 文件：server/src/main/kotlin/app/penny/services/AiService.kt
package app.penny.services

import app.penny.servershared.dto.entityDto.LedgerDto
import app.penny.servershared.enumerate.Action
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.json.Json


class AiService(
    private val openAiClient: OpenAI
) {
    private val json = Json {
        coerceInputValues = true
    }

    suspend fun getAction(
        text: String
    ): Action? {

        val actionName = getActionName(text)

        if (actionName != null) {
            return getActionDetail(actionName, text)
        }

        return null
    }





    private suspend fun getActionName(
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
            - analyzeSpending (analyze spending habits)
            - setReminder (set financial reminders)
            - none (no action required)
            
            Examples:
            - "I spent $50 at a supermarket today" => insertRecord
            - "Show me the spending records for November" => queryRecords
            - "Export my financial records from last month" => exportRecords
            - "How is the whether today?" => none
            """


        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
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


    private suspend fun getActionDetail(action: String, text: String): Action? {

        when (action) {
            "InsertLedger" -> {
                return handleInsertLedgerAction(text)
            }

        }
        return null
    }


    private suspend fun handleInsertLedgerAction(text: String): Action.InsertLedger {
        val prompt =
            """
                I am a financial assistant. The user will input natural language to describe their financial records or accounts. Your task is to extract the account name and the currency code from the user's input, and return them in JSON format.

                Supported fields:
                - name (The name of the ledger/account)
                - currencyCode (The currency code associated with the ledger/account)

                Examples:
                - "Create a new ledger called 'Expenses' in USD" => {"name": "Expenses", "currencyCode": "USD"}
                - "Add a new account for savings in EUR" => {"name": "Savings", "currencyCode": "EUR"}
                - "Set up a ledger named 'Income' with GBP as currency" => {"name": "Income", "currencyCode": "GBP"}
                - "Create a ledger for 'Investments' with the currency in USD" => {"name": "Investments", "currencyCode": "USD"}
                - "How to change the ledger's name?" => {"name": null, "currencyCode": null}
                
                If no valid name or currency code is found, return null for those fields.
                input: $text
                output:
            """.trimIndent()

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
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

        val response = completion.choices.first().message.content

        return Action.InsertLedger(
            actionDetail = response
        )

    }


}