package app.penny.models

import org.jetbrains.exposed.dao.id.IntIdTable
import kotlin.Long
import kotlin.String

object Transaction : IntIdTable("transactions") {

    val userId = long("user_id")
    val ledgerId = long("ledger_id")
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