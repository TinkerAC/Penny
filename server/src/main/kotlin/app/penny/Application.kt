package app.penny

import app.penny.config.DatabaseFactory
import app.penny.config.JwtConfig
import app.penny.routes.userRoutes
import app.penny.services.UserService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    val userService = UserService()

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
        userRoutes(userService)
    }
}