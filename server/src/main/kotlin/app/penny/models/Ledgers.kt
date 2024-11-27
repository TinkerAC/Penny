package app.penny.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object Ledgers : IntIdTable("ledgers") {
    val userId = integer("user_id").references(Users.id)
    val uuid = varchar("uuid", 255)
    val name = varchar("name", 255)
    val currencyCode = varchar("currency_code", 255)

    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}


