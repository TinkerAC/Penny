package app.penny.services

import app.penny.config.JwtConfig
import app.penny.models.Users
import app.penny.repository.UserRepository
import app.penny.servershared.dto.LoginRequest
import app.penny.servershared.dto.LoginResponse
import app.penny.servershared.dto.RegisterRequest
import org.mindrot.jbcrypt.BCrypt

class UserService {
    private val userRepository = UserRepository()

    fun register(credentials: RegisterRequest): Boolean {
        if (userRepository.findByUsername(credentials.username) != null) {
            return false
        }

        val passwordHash = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
        val userId = userRepository.insert(credentials.username, passwordHash)

        return true
    }

    fun login(credentials: LoginRequest): LoginResponse? {
        val userRow = userRepository.findByUsername(credentials.username) ?: return null
        val passwordHash = userRow[Users.passwordHash]
        if (BCrypt.checkpw(credentials.password, passwordHash)) {
            val userId = userRow[Users.id].value
            val token = JwtConfig.makeToken(userId)
            return LoginResponse(userId, credentials.username, token)
        }
        return null
    }
}