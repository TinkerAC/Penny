// 文件：server/src/main/kotlin/app/penny/repository/UserRepositoryImpl.kt
package app.penny.repository

import app.penny.servershared.dto.entityDto.UserDto

interface UserRepository {
    fun findByEmail(email: String): UserDto?
    fun insert(username: String, passwordHash: String): Long
}