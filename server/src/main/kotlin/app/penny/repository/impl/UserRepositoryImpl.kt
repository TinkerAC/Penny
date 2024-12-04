// 文件：server/src/main/kotlin/app/penny/repository/UserRepositoryImpl.kt
package app.penny.repository.impl

import app.penny.models.Users
import app.penny.models.toUserDto
import app.penny.repository.UserRepository
import app.penny.servershared.dto.entityDto.UserDto
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl : UserRepository {

    override fun findByEmail(email: String): UserDto? {
        return transaction {
            Users
                .selectAll().where { Users.email eq email }
                .map { it.toUserDto() }
                .singleOrNull()
        }
    }

    override fun insert(username: String, passwordHash: String): Long {
        return transaction {
            Users.insert {
                it[Users.email] = username
                it[Users.username] = username
                it[Users.passwordHash] = passwordHash
                val currentTime = Clock.System.now().epochSeconds
                it[createdAt] = currentTime
                it[updatedAt] = currentTime
            } get Users.id
        }.value
    }

}