// file: server/src/main/kotlin/app/penny/services/AiService.kt
package app.penny.services

import app.penny.servershared.dto.entityDto.LedgerDto
import app.penny.servershared.dto.entityDto.UserDto
import app.penny.servershared.enumerate.Action
import app.penny.utils.getUser
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.ktor.server.application.ApplicationCall
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AiService(
    private val openAiClient: OpenAI,
    private val userService: UserService
) {
    private val json = Json {
        coerceInputValues = true
    }

    /**
     * Retrieves the action based on user input text.
     * The user is retrieved from the ApplicationCall attributes.
     */
    suspend fun getAction(
        call: ApplicationCall,
        text: String
    ): Action? {
        val user = call.getUser()
        if (user == null) {
            // Optionally handle the absence of a user, e.g., throw an exception
            return null
        }

        val actionName = getActionName(text)

        return if (actionName != null) {
            getActionDetail(actionName, text, user)
        } else {
            null
        }
    }

    /**
     * Identifies the action name from the user input text using OpenAI.
     */
    private suspend fun getActionName(text: String): String? {
        val prompt = """
            I am a financial assistant. The user will input natural language to describe accounting tasks. Your task is to identify the user's intent and return the corresponding method name.
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
            - "I spent $50 at a supermarket today" => insertLedgerRecord
            - "Show me the spending records for November" => queryRecords
            - "Export my financial records from last month" => exportRecords
            - "How is the weather today?" => none
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

        return completion.choices.firstOrNull()?.message?.content?.trim()
    }

    /**
     * Retrieves detailed action information based on the action name.
     */
    private suspend fun getActionDetail(action: String, text: String, user: UserDto): Action? {
        return when (action) {
            "insertLedgerRecord" -> handleInsertLedgerAction(user, text)
            // Add handlers for other actions similarly
            // "insertTransactionRecord" -> handleInsertTransactionAction(user, text)
            // "queryRecords" -> handleQueryRecordsAction(user, text)
            // ... other actions
            else -> null // Handle unknown or unsupported actions
        }
    }

    /**
     * Handles the 'insertLedgerRecord' action by extracting necessary details.
     */
    private suspend fun handleInsertLedgerAction(
        user: UserDto,
        text: String
    ): Action.InsertLedger? {
        val prompt = """
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

        val response = completion.choices.firstOrNull()?.message?.content?.trim()

        if (response.isNullOrBlank()) {
            return null
        }

        // Parse JSON response
        val jsonObject = try {
            json.parseToJsonElement(response).jsonObject
        } catch (e: Exception) {
            // Handle JSON parsing exceptions
            null
        }

        val name = jsonObject?.get("name")?.jsonPrimitive?.contentOrNull
        val currencyCode = jsonObject?.get("currencyCode")?.jsonPrimitive?.contentOrNull



        return Action.InsertLedger(
            dto = LedgerDto.create(
                userUuid = user.uuid,
                name = name ?: "",
                currencyCode = currencyCode ?: ""
            )
        )
    }
}
