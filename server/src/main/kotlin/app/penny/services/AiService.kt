// file: server/src/main/kotlin/app/penny/services/AiService.kt
package app.penny.services

import app.penny.core.domain.enum.Category
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.MonthlyReportData
import app.penny.servershared.dto.TransactionDto
import app.penny.servershared.dto.UserDto
import app.penny.servershared.enumerate.UserIntent
import app.penny.utils.getAuthedUser
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.ktor.server.application.ApplicationCall
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AiService(
    private val openAiClient: OpenAI, private val userService: UserService
) {
    private val json = Json {
        coerceInputValues = true
    }

    /**
     * Retrieves the userIntent based on user input text.
     * The user is retrieved from the ApplicationCall attributes.
     */
    suspend fun getUserIntent(
        call: ApplicationCall, text: String, invokeInstant: Long, userTimeZoneId: String
    ): UserIntent {
        val user = call.getAuthedUser()
            ?: // Optionally handle the absence of a user, e.g., throw an exception
            return UserIntent.JustTalk()

        val inferredUserIntent = inferUserIntent(text)
        return getActionDetail(
            inferredUserIntent, text, user, invokeInstant, userTimeZoneId
        )
    }

    /**
     * Identifies the userIntent name from the user input text using OpenAI.
     */
    private suspend fun inferUserIntent(text: String): UserIntent {
        val prompt = UserIntent.generatePrompt()
//        println(prompt)
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"), messages = listOf(
                ChatMessage(
                    role = ChatRole.System, content = prompt
                ), ChatMessage(
                    role = ChatRole.User, content = text
                )
            )
        )

        val completion: ChatCompletion = openAiClient.chatCompletion(chatCompletionRequest)

        val response = completion.choices.firstOrNull()?.message?.content?.trim()


        //kotlin reflect not support in multiplatform


        return response?.let {
            UserIntent.fromKClassSimpleName(
                it
            )
        } ?: UserIntent.JustTalk()
    }

    /**
     * Retrieves detailed userIntent information based on the userIntent name.
     */
    private suspend fun getActionDetail(
        userIntent: UserIntent, text: String, user: UserDto, invokeInstant: Long, //epoch seconds
        userTimeZoneId: String
    ): UserIntent {

        val detailedUserIntent: UserIntent? = when (userIntent) {
            is UserIntent.InsertLedger -> handleInsertLedgerAction(user, text)
            is UserIntent.InsertTransaction -> handleInsertTransactionAction(
                user, text, invokeInstant, userTimeZoneId
            )

            is UserIntent.JustTalk -> handleJustTalkAction(user, text)
            is UserIntent.GenerateMonthlyReport -> handleGenerateReportAction(
                user,
                text,
                invokeInstant,
                userTimeZoneId
            )

            else -> null
        }

        val notNullUserIntent: UserIntent = detailedUserIntent ?: handleJustTalkAction(user, text)

        return notNullUserIntent
    }

    private suspend fun handleGenerateReportAction(
        user: UserDto,
        text: String,
        invokeInstant: Long,
        userTimeZoneId: String
    ): UserIntent? {
        // 根据 userTimeZoneId 构建 TimeZone
        // 假设 userTimeZoneId 表示与 UTC 的小时偏移量（如UTC+8）
        val userTimeZone = TimeZone.of(zoneId = userTimeZoneId)

        // 将 invokeInstant 转为用户本地日期
        val userLocalDate: LocalDate =
            Instant.fromEpochSeconds(invokeInstant).toLocalDateTime(userTimeZone).date

        val prompt = """
            [Role]
            You are a diligent financial assistant who's name is Penny, a Diligent and cute fairy.
            [Goal]
            The user will input natural language to describe the Month they want to generate a financial report for
            and a localDate of the user as a reference.
            Your task is to extract the month and year from the user's input, and return them in JSON format.
            
            Supported fields:
            - month (The month for which the user wants to generate a report)
            - year (The year for which the user wants to generate a report)
            
            [Additional Information]
            If no valid month or year is found, return null for those fields.
            
            [Examples]
            
            ex1:
            text: "Generate a report for the month of November" 
            userLocalDate: 2024-11-01
            => {"month": 11, "year": 2024}
             
            ex2:
            text: "Generate a report of last month"
            userLocalDate: 2024-11-01
            => {"month": 10, "year": 2024}
            
            [Note]
            Keep in mind that the userLocalDate is the reference date of the user,and return JSON format
        """.trimIndent()

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"), messages = listOf(
                ChatMessage(
                    role = ChatRole.System, content = prompt
                ), ChatMessage(
                    role = ChatRole.User, content = "text:$text \n userLocalDate:$userLocalDate"
                )
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

        val month = jsonObject?.get("month")?.jsonPrimitive?.contentOrNull
        val year = jsonObject?.get("year")?.jsonPrimitive?.contentOrNull

        return UserIntent.GenerateMonthlyReport(
            month = month?.toIntOrNull() ?: 0,
            year = year?.toIntOrNull() ?: 0
        )
    }

    /**
     * Handles the 'insertLedgerRecord' userIntent by extracting necessary details.
     */
    private suspend fun handleInsertLedgerAction(
        user: UserDto, text: String
    ): UserIntent.InsertLedger? {
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
            - "新建人民币账本, 用于我的宝宝支出" => {"name": "宝宝支出", "currencyCode": "CNY"}
            - "Set up a ledger named 'Income' with GBP as currency" => {"name": "Income", "currencyCode": "GBP"}
            - "Create a ledger for 'Investments' with the currency in USD" => {"name": "Investments", "currencyCode": "USD"}
            - "How to change the ledger's name?" => {"name": null, "currencyCode": null}
        """.trimIndent()

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"), messages = listOf(
                ChatMessage(
                    role = ChatRole.System, content = prompt
                ), ChatMessage(
                    role = ChatRole.User, content = text
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



        return UserIntent.InsertLedger(
            dto = LedgerDto.create(
                userUuid = user.uuid, name = name ?: "", currencyCode = currencyCode ?: ""
            )
        )
    }


    @OptIn(ExperimentalUuidApi::class)
    private suspend fun handleInsertTransactionAction(
        user: UserDto, text: String, invokeInstant: Long, // epoch seconds
        userTimeZoneId: String
    ): UserIntent.InsertTransaction? {

        // 根据 userTimeZoneId 构建 TimeZone
        // 假设 userTimeZoneId 表示与 UTC 的小时偏移量（如UTC+8）
        val userTimeZone = TimeZone.of(zoneId = userTimeZoneId)

        // 将 invokeInstant 转为用户本地日期
        val userLocalDate: LocalDate =
            Instant.fromEpochSeconds(invokeInstant).toLocalDateTime(userTimeZone).date


        val prompt = Category.generateLLMPrompt()

        println("prompt: $prompt")
        val input = "text:$text \n userLocalDate:$userLocalDate"
        println("input: $input")

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"), messages = listOf(
                ChatMessage(role = ChatRole.System, content = prompt), ChatMessage(
                    role = ChatRole.User, content = input

                )
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

        return UserIntent.InsertTransaction(
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


    private suspend fun handleJustTalkAction(
        user: UserDto, text: String
    ): UserIntent.JustTalk {
        val prompt = """
            [Role]
            You are a friendly financial assistant who's name is Penny, a Diligent and cute fairy.
            [Goal]
            The user will input natural language to chat with you. Your task is to respond to the user's input in a friendly and helpful manner.
            [Examples]
            - "How are you today?" => "I'm doing well, thank you for asking! How can I help you today?"
            - "What's the weather like today?" => "I'm not sure about the weather, but I can help you with your finances!"
            - "Tell me a joke" => "Sure! Why did the banker switch careers? He lost interest!"
            - "What's the meaning of life?" => "The meaning of life is to enjoy the journey and make the most of every moment!"
            
        """.trimIndent()

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"), messages = listOf(
                ChatMessage(
                    role = ChatRole.System, content = prompt
                ), ChatMessage(
                    role = ChatRole.User, content = text
                )
            )
        )

        val completion: ChatCompletion = openAiClient.chatCompletion(chatCompletionRequest)

        val response = completion.choices.firstOrNull()?.message?.content?.trim()

        return UserIntent.JustTalk(
            aiReplyText = response
        )
    }


    suspend fun generateReport(
        data: MonthlyReportData
    ): String? {
        val prompt = """
            [Role]
            You are a diligent financial assistant who's name is Penny, a Diligent and cute fairy.

            [Goal]
            The user will input their financial data for a specific month, and your task is to generate a natural language financial report based on this data.

            Infos to be provided:
            - totalIncome (The total income for the month)
            - totalExpense (The total expenses for the month)
            - totalBalance (The total balance for the month)
            - incomeCategories (A breakdown of income sources and their percentages)
            - expenseCategories (A breakdown of expenses by category and their percentages)
            - averageExpense (The average daily expense for the month)
            - largestExpense (The largest single expense, including its category and description)
            
            [Additional Information]
            Use the provided data to generate a clear and concise monthly financial report in natural language, including trends, insights, and recommendations for the next month.
            [Examples]
            ex1:
            data: {
                "totalIncome": 5000,
                "totalExpense": 4000,
                "netSavings": 1000,
                "incomeCategories": [
                    {"category": "Salary", "amount": 4500, "percentage": 90},
                    {"category": "Investment", "amount": 500, "percentage": 10}
                ],
                "expenseCategories": [
                    {"category": "Food", "amount": 1200, "percentage": 30},
                    {"category": "Rent", "amount": 2000, "percentage": 50},
                    {"category": "Entertainment", "amount": 800, "percentage": 20}
                ],
                "
                "savingsTarget": {"targetAmount": 2000, "currentSavings": 1000, "targetMet": false},
                "largestExpense": {"category": "Rent", "amount": 2000, "description": "Monthly apartment rent"},
                "userLocalDate": "2024-11-01"
            }
            => "In November 2024, your total income was 5000 and your total expenses were 4000, leaving you with net savings of 1000. The majority of your income came from your salary (90%). Your largest expense was on rent (2000), which accounted for 50% of your expenses. You spent 30% on food and 20% on entertainment. You saved 1000 towards your goal of 2000, meaning you are halfway there. Daily spending was consistent, with a slight peak on November 3. Next month, consider limiting entertainment expenses to increase savings."
                         
            [Note]
            Use the provided data fields only and generate output strictly based on them. Do not introduce new fields or assumptions.
        """.trimIndent()

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"), messages = listOf(
                ChatMessage(
                    role = ChatRole.System, content = prompt,
                ),
                ChatMessage(
                    role = ChatRole.User, content = json.encodeToString(
                        MonthlyReportData.serializer(), data
                    )
                )
            )
        )

        val completion: ChatCompletion = openAiClient.chatCompletion(chatCompletionRequest)

        val response = completion.choices.firstOrNull()?.message?.content?.trim()

        return response
    }


}