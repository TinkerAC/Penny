// file: server/src/main/kotlin/app/penny/Application.kt
package app.penny

import app.penny.config.DatabaseFactory
import app.penny.config.JwtConfig
import app.penny.repository.LedgerRepository
import app.penny.repository.TransactionRepository
import app.penny.repository.UserRepository
import app.penny.repository.impl.LedgerRepositoryImpl
import app.penny.repository.impl.TransactionRepositoryImpl
import app.penny.repository.impl.UserRepositoryImpl
import app.penny.routes.aiRoutes
import app.penny.routes.authRoutes
import app.penny.routes.syncRoutes
import app.penny.routes.userRoutes
import app.penny.services.AiService
import app.penny.services.AuthService
import app.penny.services.LedgerService
import app.penny.services.StatisticsService
import app.penny.services.TransactionService
import app.penny.services.UserService
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.aallam.openai.client.ProxyConfig
import com.typesafe.config.ConfigFactory
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

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
        host = OpenAIHost(config.getString("openai.host")),
        proxy = ProxyConfig.Http(config.getString("openai.proxy"))
        // Additional configurations if needed
    )
    val aiService = AiService(openAiClient, userService)
    val ledgerService = LedgerService(ledgerRepository)
    val transactionService = TransactionService(transactionRepository)
    val statisticsService = StatisticsService(ledgerRepository, transactionRepository)
    val authService = AuthService(jwtConfig, userRepository = userRepository)

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
