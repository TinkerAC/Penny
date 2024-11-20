package app.penny.models

import org.jetbrains.exposed.dao.id.IntIdTable

object User : IntIdTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 64)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}