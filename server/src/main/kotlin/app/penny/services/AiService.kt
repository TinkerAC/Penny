// file: server/src/main/kotlin/app/penny/services/AiService.kt
package app.penny.services

import app.penny.core.domain.enum.Category
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.TransactionDto
import app.penny.servershared.dto.UserDto
import app.penny.servershared.enumerate.Action
import app.penny.utils.getAuthedUser

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.mysql.cj.log.Log
import io.ktor.server.application.ApplicationCall
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
        text: String,
        invokeInstant: Long,
        userTimeZoneId: String

    ): Action? {
        val user = call.getAuthedUser()
        if (user == null) {
            // Optionally handle the absence of a user, e.g., throw an exception
            return null
        }

        val actionName = getActionName(text)

        return if (actionName != null) {
            getActionDetail(
                action = actionName,
                text = text,
                user = user,
                invokeInstant = invokeInstant,
                userTimeZoneId = userTimeZoneId
            )
        } else {
            null
        }
    }

    /**
     * Identifies the action name from the user input text using OpenAI.
     */
    private suspend fun getActionName(text: String): String? {
        val prompt = """
            [Role]
            You are a financial assistant.
            [Goal]
            The user will input natural language to describe accounting tasks. 
            Your goal is to identify the user's intent and return the corresponding method name listed below.
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
            [Examples]
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
    private suspend fun getActionDetail(
        action: String,
        text: String,
        user: UserDto,
        invokeInstant: Long, //epoch seconds
        userTimeZoneId: String
    ): Action? {
        return when (action) {
            "insertLedgerRecord" -> handleInsertLedgerAction(user, text)
            // Add handlers for other actions similarly
            "insertTransactionRecord" -> handleInsertTransactionAction(
                user = user,
                text = text,
                invokeInstant = invokeInstant,
                userTimeZoneId = userTimeZoneId
            )
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
            [Role]
            You are a helpful financial assistant 
            [Goal]
            The user will input natural language to describe their financial records or accounts. Your task is to extract the account name and the currency code from the user's input, and return them in JSON format.
    
            Supported fields:
            - name (The name of the ledger/account)
            - currencyCode (The currency code associated with the ledger/account, should be in ISO 4217 format)
            
            [Additional Information]
            If no valid name or currency code is found, return null for those fields.
            
            [Examples]
            - "Create a new ledger called 'Expenses' in USD" => {"name": "Expenses", "currencyCode": "USD"}
            - "Add a new account for savings in EUR" => {"name": "Savings", "currencyCode": "EUR"}
            - "新建人名币账本, 用于我的宝宝支出" => {"name": "宝宝支出", "currencyCode": "CNY"}
            - "Set up a ledger named 'Income' with GBP as currency" => {"name": "Income", "currencyCode": "GBP"}
            - "Create a ledger for 'Investments' with the currency in USD" => {"name": "Investments", "currencyCode": "USD"}
            - "How to change the ledger's name?" => {"name": null, "currencyCode": null}
            
            
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


    @OptIn(ExperimentalUuidApi::class)
    private suspend fun handleInsertTransactionAction(
        user: UserDto,
        text: String,
        invokeInstant: Long, // epoch seconds
        userTimeZoneId: String
    ): Action.InsertTransaction? {

        // 根据 userTimeZoneId 构建 TimeZone
        // 假设 userTimeZoneId 表示与 UTC 的小时偏移量（如UTC+8）
        val userTimeZone = TimeZone.of(zoneId = userTimeZoneId)

        // 将 invokeInstant 转为用户本地日期
        val userLocalDate: LocalDate = Instant.fromEpochSeconds(invokeInstant)
            .toLocalDateTime(userTimeZone)
            .date

        val prompt = Category.generateLLMPrompt(
            text = text,
            userLocalDate = userLocalDate.toString()
        )

        println("prompt: $prompt")

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            messages = listOf(
                ChatMessage(role = ChatRole.System, content = prompt),
                ChatMessage(role = ChatRole.User, content = text)
            )
        )

        val completion: ChatCompletion = openAiClient.chatCompletion(chatCompletionRequest)
        val response = completion.choices.firstOrNull()?.message?.content?.trim()

        if (response.isNullOrBlank()) {
            return null
        }

        val jsonObject = try {
            json.parseToJsonElement(response).jsonObject
        } catch (e: Exception) {
            null
        }

        val amount = jsonObject?.get("amount")?.jsonPrimitive?.contentOrNull
        val remark = jsonObject?.get("remark")?.jsonPrimitive?.contentOrNull
        val categoryName = jsonObject?.get("category")?.jsonPrimitive?.contentOrNull
        val transactionType = jsonObject?.get("transactionType")?.jsonPrimitive?.contentOrNull
        val rawTransactionDate = jsonObject?.get("transactionDate")?.jsonPrimitive?.contentOrNull

        // 根据用户时区，将日期转换为当地午夜的epochSeconds
        val finalTransactionDate: Long = if (!rawTransactionDate.isNullOrBlank()) {
            val parsedDate = LocalDate.parse(rawTransactionDate)
            val instant: Instant = parsedDate.atStartOfDayIn(userTimeZone)
            instant.epochSeconds
        } else {
            // 未提供日期则使用invokeInstant对应的日期的当地午夜时间
            userLocalDate.atStartOfDayIn(userTimeZone).epochSeconds
        }

        return Action.InsertTransaction(
            dto = TransactionDto(
                userId = user.id,
                uuid = Uuid.random().toString(),
                ledgerUuid = "",
                transactionType = transactionType ?: "",
                transactionDate = finalTransactionDate,
                categoryName = categoryName ?: "",
                currencyCode = "",
                amount = amount ?: "",
                remark = remark ?: "",
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds()
            )
        )
    }


}