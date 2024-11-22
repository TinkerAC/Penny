package app.penny.core.domain.model

import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.Currency
import app.penny.core.domain.enum.TransactionType
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant

data class TransactionModel(
    val ledgerId: Long = 0,
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
        return "TransactionModel(ledgerId=$ledgerId, transactionDate=$transactionDate, category=$category, transactionType=$transactionType, amount=${amount.toPlainString()}, currency=$currency, screenshotUri=$screenshotUri, remark=$remark, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}



