// file: server/src/main/kotlin/app/penny/Application.kt
package app.penny

import app.penny.config.DatabaseFactory
import app.penny.config.JwtConfig
import app.penny.repository.*
import app.penny.repository.impl.*
import app.penny.routes.*
import app.penny.services.*
import app.penny.utils.UserKey
import app.penny.utils.getUser
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.http.HttpStatusCode

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    // Load configuration
    val config = ConfigFactory.load()

    // Initialize database
    DatabaseFactory.init()

    // Create repository instances
    val userRepository: UserRepository = UserRepositoryImpl()
    val ledgerRepository: LedgerRepository = LedgerRepositoryImpl()
    val transactionRepository: TransactionRepository = TransactionRepositoryImpl()

    // Initialize JWT configuration
    val jwtConfig = JwtConfig

    // Create service instances
    val userService = UserService(userRepository, jwtConfig)
    val openAiClient = OpenAI(
        token = config.getString("openai.apiKey"),
        host = OpenAIHost(config.getString("openai.host"))
        // Additional configurations if needed
    )
    val aiService = AiService(openAiClient, userService)
    val ledgerService = LedgerService(ledgerRepository)
    val transactionService = TransactionService(transactionRepository)
    val statisticsService = StatisticsService(ledgerRepository, transactionRepository)
    val authService = AuthService(jwtConfig)

    // Install Content Negotiation
    install(ContentNegotiation) {
        json()
    }

    // Install Authentication
    install(Authentication) {
        jwt("access-jwt") {
            verifier(JwtConfig.accessTokenVerifier)
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asLong()
                if (userId != null) {
                    val user = userService.findUserById(userId)
                    if (user != null) {
                        // Store the user in call.attributes for later retrieval
                        credential.context.attributes.put(UserKey, user)
                        JWTPrincipal(credential.payload)
                    } else {
                        null // Invalid user
                    }
                } else {
                    null // No userId claim
                }
            }
        }
    }

    // Set up routing
    routing {
        authRoutes(authService)
        userRoutes(userService)
        syncRoutes(ledgerService, transactionService, statisticsService)
        aiRoutes(aiService) // Updated aiRoutes to no longer require userService
    }
}
