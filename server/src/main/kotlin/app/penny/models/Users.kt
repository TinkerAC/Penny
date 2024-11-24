package app.penny.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable("users") {
    val email = varchar("email", 50).uniqueIndex()
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 64)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var email by Users.email

    var username by Users.username
    var passwordHash by Users.passwordHash
    var createdAt by Users.createdAt
    var updatedAt by Users.updatedAt
}