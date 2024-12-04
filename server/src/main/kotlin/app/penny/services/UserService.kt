// 文件：server/src/main/kotlin/app/penny/services/UserService.kt
package app.penny.services

import app.penny.config.JwtConfig
import app.penny.repository.UserRepository
import app.penny.servershared.dto.requestDto.LoginRequest
import app.penny.servershared.dto.responseDto.LoginResponse
import app.penny.servershared.dto.requestDto.RegisterRequest
import app.penny.servershared.dto.entityDto.UserDto
import org.mindrot.jbcrypt.BCrypt

class UserService(
    private val userRepository: UserRepository,
    private val jwtConfig: JwtConfig
) {
    fun register(credentials: RegisterRequest): Boolean {
        if (userRepository.findByEmail(credentials.email) != null) {
            return false
        }

        val passwordHash = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
        userRepository.insert(credentials.email, passwordHash)
        return true
    }

    fun login(credentials: LoginRequest): LoginResponse {
        val userEntity = userRepository.findByEmail(credentials.email)
            ?: return LoginResponse(
                success = false,
                userDto = null,
                message = "Invalid email or password"
            )

        return if (BCrypt.checkpw(credentials.password, userEntity.passwordHash)) {
            val userId = userEntity.id
            val accessToken = jwtConfig.makeAccessToken(userId)
            val refreshToken = jwtConfig.makeRefreshToken(userId)
            LoginResponse(
                success = true,
                message = "Login successful",
                accessToken = accessToken,
                refreshToken = refreshToken,
                userDto = userEntity.toUserResponseDto()
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

// 扩展函数，用于转换 UserEntity 到 UserResponseDto
fun UserDto.toUserResponseDto(): UserDto {
    return UserDto(
        id = id,
        username = username,
        email = email,
        )
}

