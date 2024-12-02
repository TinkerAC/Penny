// 文件：server/src/main/kotlin/app/penny/Application.kt
package app.penny

import app.penny.config.DatabaseFactory
import app.penny.repository.*
import app.penny.repository.impl.*
import app.penny.routes.*
import app.penny.services.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import app.penny.config.JwtConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import com.typesafe.config.ConfigFactory

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    val config = ConfigFactory.load()

    DatabaseFactory.init()


    //install plugin
    install(ContentNegotiation) {
        json(

        )
    }
    install(Authentication) {
        jwt("access-jwt") {
            verifier(JwtConfig.accessTokenVerifier)
            validate {
                val userId = it.payload.getClaim("userId").asInt()
                if (userId != null) {
                    JWTPrincipal(it.payload)
                } else null
            }
        }
    }


    // 初始化配置
    val jwtConfig = JwtConfig

    // 创建仓库实例
    val userRepository: UserRepository = UserRepositoryImpl()
    val ledgerRepository: LedgerRepository = LedgerRepositoryImpl()
    val transactionRepository: TransactionRepository = TransactionRepositoryImpl()

    // 创建OpenAI客户端
    val openAiClient = OpenAI(
        token = config.getString("openai.apiKey"),
        host = OpenAIHost(config.getString("openai.host"))

        // additional configurations...
    )


    // 创建服务实例

    val aiService = AiService(openAiClient)
    val userService = UserService(userRepository, jwtConfig)
    val ledgerService = LedgerService(ledgerRepository)
    val transactionService = TransactionService(transactionRepository)
    val statisticsService = StatisticsService(ledgerRepository, transactionRepository)
    val authService = AuthService(jwtConfig)

    // 设置路由
    routing {
        authRoutes(authService)
        userRoutes(userService)
        syncRoutes(ledgerService, transactionService, statisticsService)
        aiRoutes(aiService)
    }
}