package app.penny.repository

import app.penny.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun findByEmail(email: String): ResultRow? {
        return transaction {
            Users.selectAll().where { Users.email eq email }.singleOrNull()
        }
    }

    fun insert(username: String, passwordHash: String): Long {
        return transaction {
            Users.insert {
                it[Users.email] = username
                it[Users.username] = username
                it[Users.passwordHash] = passwordHash
                it[createdAt] = System.currentTimeMillis()
                it[updatedAt] = System.currentTimeMillis()
            } get Users.id
        }.value
    }
}