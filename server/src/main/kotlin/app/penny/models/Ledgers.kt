package app.penny.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object Ledgers : IntIdTable("ledgers") {
    val uuid = varchar("uuid", 255)
    val name = varchar("name", 255)
    val currencyCode = varchar("currency_code", 255)

    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}



class Ledger(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Ledger>(Ledgers)

    var uuid by Ledgers.uuid
    var name by Ledgers.name
    var currencyCode by Ledgers.currencyCode
    var createdAt by Ledgers.createdAt
    var updatedAt by Ledgers.updatedAt
}