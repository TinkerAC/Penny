package app.penny.services

import app.penny.models.User
import app.penny.models.dto.UserCredentials
import app.penny.models.dto.UserResponse
import app.penny.repository.UserRepository
import app.penny.config.JwtConfig
import org.jetbrains.exposed.sql.ResultRow
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
        val passwordHash = userRow[User.passwordHash]
        if (BCrypt.checkpw(credentials.password, passwordHash)) {
            val userId = userRow[User.id].value
            val token = JwtConfig.makeToken(userId)
            return UserResponse(userId, credentials.username, token)
        }
        return null
    }
}