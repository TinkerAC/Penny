package app.penny.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Transactions : IntIdTable("transactions") {

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

class Transaction(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Transaction>(Transactions)

    var userId by Transactions.userId
    var ledgerId by Transactions.ledgerId
    var transactionDate by Transactions.transactionDate
    var categoryName by Transactions.categoryName
    var transactionType by Transactions.transactionType
    var amount by Transactions.amount
    var currencyCode by Transactions.currencyCode
    var remark by Transactions.remark
    var screenshotUri by Transactions.screenshotUri
    var createdAt by Transactions.createdAt
    var updatedAt by Transactions.updatedAt

}

