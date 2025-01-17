package app.penny.core.domain.model

import app.penny.core.domain.enumerate.Category
import app.penny.core.domain.enumerate.Currency
import app.penny.core.domain.enumerate.TransactionType
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class TransactionModel(
    var uuid: Uuid = Uuid.parse("00000000-0000-0000-0000-000000000000"),
    var ledgerUuid: Uuid = Uuid.parse("00000000-0000-0000-0000-000000000000"),
    val transactionInstant: Instant = Instant.DISTANT_PAST,
    val category: Category = Category.EXPENSE,
    val transactionType: TransactionType = TransactionType.EXPENSE,
    var amount: BigDecimal = BigDecimal.ZERO,
    val currency: Currency = Currency.USD,
    val screenshotUri: String? = null,
    val remark: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,


    ) {
    override fun toString(): String {
        return "TransactionModel(ledgerUuid =$ledgerUuid, transactionDate=$transactionInstant, category=$category, transactionType=$transactionType, amount=${amount.toPlainString()}, currency=$currency, screenshotUri=$screenshotUri, remark=$remark, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}



