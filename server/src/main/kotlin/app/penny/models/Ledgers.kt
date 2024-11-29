package app.penny.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable


object Ledgers : LongIdTable("ledgers") {
    val userId = reference("user_id", Users.id)
    val uuid = varchar("uuid", 36).uniqueIndex()
    val name = varchar("name", 255)
    val currencyCode = varchar("currency_code", 3)

    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}


