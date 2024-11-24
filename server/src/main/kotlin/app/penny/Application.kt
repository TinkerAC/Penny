package app.penny

import app.penny.config.DatabaseFactory
import app.penny.config.JwtConfig
import app.penny.routes.syncRoutes
import app.penny.routes.userRoutes
import app.penny.services.LedgerService
import app.penny.services.UserService
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.ktor.server.routing.post
import io.ktor.server.routing.get

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    val userService = UserService()
    val ledgerService = LedgerService()

    install(ContentNegotiation) {
        json()
    }

    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            validate {
                val userId = it.payload.getClaim("userId").asInt()
                if (userId != null) {
                    UserIdPrincipal(userId.toString())
                } else null
            }
        }
    }

    routing {
        //greeting route
        get("/") {
            call.respondText("Welcome to Ktor!")
        }


        //functional routes
        userRoutes(userService, jwtConfig = JwtConfig)
        syncRoutes(ledgerService, jwtConfig = JwtConfig)

    }
}