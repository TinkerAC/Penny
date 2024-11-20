package app.penny.repository

import app.penny.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun findByUsername(username: String): ResultRow? {
        return transaction {
            User.selectAll().where { User.username eq username }.singleOrNull()
        }
    }

    fun insert(username: String, passwordHash: String): Int {
        return transaction {
            User.insert {
                it[User.username] = username
                it[User.passwordHash] = passwordHash
                it[createdAt] = System.currentTimeMillis()
                it[updatedAt] = System.currentTimeMillis()
            } get User.id
        }.value
    }
}