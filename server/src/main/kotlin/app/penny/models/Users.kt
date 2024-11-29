package app.penny.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable("users") {
    val email = varchar("email", 254).uniqueIndex()
    val username = varchar("username", 50)
    val passwordHash = varchar("password_hash", 255)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}
