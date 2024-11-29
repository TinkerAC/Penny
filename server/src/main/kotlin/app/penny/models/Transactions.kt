package app.penny.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Transactions : LongIdTable("transactions") {
    val userId = reference("user_id", Users.id)
    val uuid = varchar("uuid", 255)
    val ledgerUuid = varchar("ledger_uuid", 255)
    val transactionDate = long("transaction_date")
    val categoryName = varchar("category_name", 255)
    val transactionType = varchar("transaction_type", 255)
    val amount = varchar("amount", 255)
    val currencyCode = varchar("currency_code", 255)
    val remark = varchar("remark", 255).nullable()
    val screenshotUri = varchar("screenshot_uri", 255).nullable()
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}


