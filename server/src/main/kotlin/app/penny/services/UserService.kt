package app.penny.services

import app.penny.models.Users
import app.penny.repository.UserRepository
import app.penny.config.JwtConfig
import org.mindrot.jbcrypt.BCrypt

class UserService {
    private val userRepository = UserRepository()

    fun register(credentials: UserCredentials): UserResponse? {
        if (userRepository.findByUsername(credentials.username) != null) {
            return null // 用户已存在
        }
        val passwordHash = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
        val userId = userRepository.insert(credentials.username, passwordHash)
        val token = JwtConfig.makeToken(userId)
        return UserResponse(userId, credentials.username, token)
    }

    fun login(credentials: UserCredentials): UserResponse? {
        val userRow = userRepository.findByUsername(credentials.username) ?: return null
        val passwordHash = userRow[Users.passwordHash]
        if (BCrypt.checkpw(credentials.password, passwordHash)) {
            val userId = userRow[Users.id].value
            val token = JwtConfig.makeToken(userId)
            return UserResponse(userId, credentials.username, token)
        }
        return null
    }
}