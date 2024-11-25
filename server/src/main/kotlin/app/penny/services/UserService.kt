// file: server/src/main/kotlin/app/penny/services/UserService.kt
package app.penny.services

import app.penny.config.JwtConfig
import app.penny.models.Users
import app.penny.repository.UserRepository
import app.penny.servershared.dto.LoginRequest
import app.penny.servershared.dto.LoginResponse
import app.penny.servershared.dto.RegisterRequest
import app.penny.servershared.dto.UserDto
import org.mindrot.jbcrypt.BCrypt

class UserService(private val jwtConfig: JwtConfig) {
    private val userRepository = UserRepository()

    fun register(credentials: RegisterRequest): Boolean {
        if (userRepository.findByEmail(credentials.email) != null) {
            return false
        }

        val passwordHash = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
        userRepository.insert(credentials.email, passwordHash)
        return true
    }

    fun login(credentials: LoginRequest): LoginResponse {
        val userRow = userRepository.findByEmail(credentials.email) ?: return LoginResponse(
            success = false,
            userDto = null,
            message = "Invalid email or password"
        )
        val passwordHash = userRow[Users.passwordHash]
        return if (BCrypt.checkpw(credentials.password, passwordHash)) {
            val userId = userRow[Users.id].value
            val token = jwtConfig.makeAccessToken(userId)
            LoginResponse(
                success = true,
                accessToken = token,
                userDto = UserDto(
                    id = userId.toLong(),
                    username = userRow[Users.username],
                    email = userRow[Users.email]
                )
            )
        } else {
            LoginResponse(
                success = false,
                accessToken = null,
                userDto = null,
                message = "Invalid email or password"
            )
        }
    }

    fun checkIsEmailRegistered(email: String): Boolean {
        return userRepository.findByEmail(email) != null
    }
}
