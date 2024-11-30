package app.penny.core.domain.model

import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.Currency
import app.penny.core.domain.enum.TransactionType
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class TransactionModel(
    var uuid: Uuid = Uuid.parse("00000000-0000-0000-0000-000000000000"),
    var ledgerUuid: Uuid = Uuid.parse("00000000-0000-0000-0000-000000000000"),
    val transactionDate: Instant,
    val category: Category = Category.MISCELLANEOUS,
    val transactionType: TransactionType = TransactionType.EXPENSE,
    var amount: BigDecimal = BigDecimal.ZERO,
    val currency: Currency = Currency.USD,
    val screenshotUri: String? = null,
    val remark: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,


    ) {
    override fun toString(): String {
        return "TransactionModel(ledgerUuid =$ledgerUuid, transactionDate=$transactionDate, category=$category, transactionType=$transactionType, amount=${amount.toPlainString()}, currency=$currency, screenshotUri=$screenshotUri, remark=$remark, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}



