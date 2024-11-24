package app.penny.services

import app.penny.config.JwtConfig
import app.penny.models.Users
import app.penny.repository.UserRepository
import app.penny.servershared.dto.LoginRequest
import app.penny.servershared.dto.LoginResponse
import app.penny.servershared.dto.RegisterRequest
import app.penny.servershared.dto.UserDto
import org.mindrot.jbcrypt.BCrypt

class UserService {
    private val userRepository = UserRepository()

    fun register(credentials: RegisterRequest): Boolean {
        if (userRepository.findByEmail(credentials.email) != null) {
            return false
        }

        val passwordHash = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
        val userId = userRepository.insert(credentials.email, passwordHash)

        return true
    }

    fun login(credentials: LoginRequest): LoginResponse {
        val userRow = userRepository.findByEmail(credentials.email) ?: return LoginResponse(
            success = false,
            token = null,
            userDto = null
        )
        val passwordHash = userRow[Users.passwordHash]
        if (BCrypt.checkpw(credentials.password, passwordHash)) {
            val userId = userRow[Users.id].value
            val token = JwtConfig.makeToken(userId)
            return LoginResponse(
                success = true,
                token = token,
                userDto = UserDto(
                    id = userRow[Users.id].value.toLong(),
                    username = userRow[Users.username],
                    email = userRow[Users.email]
                )

            )
        } else
            return LoginResponse(
                success = false,
                token = null,
                userDto = null,
                message = "Invalid email or password"
            )

    }


    fun checkIsEmailRegistered(email: String): Boolean {
        return userRepository.findByEmail(email) != null
    }
}