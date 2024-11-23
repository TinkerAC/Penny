package app.penny.repository

import app.penny.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun findByUsername(username: String): ResultRow? {
        return transaction {
            Users.selectAll().where { Users.username eq username }.singleOrNull()
        }
    }

    fun insert(username: String, passwordHash: String): Int {
        return transaction {
            Users.insert {
                it[Users.username] = username
                it[Users.passwordHash] = passwordHash
                it[createdAt] = System.currentTimeMillis()
                it[updatedAt] = System.currentTimeMillis()
            } get Users.id
        }.value
    }
}