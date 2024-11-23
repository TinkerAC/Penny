package app.penny.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 64)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var username by Users.username
    var passwordHash by Users.passwordHash
    var createdAt by Users.createdAt
    var updatedAt by Users.updatedAt
}