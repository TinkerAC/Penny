package app.penny.domain.model

import app.penny.domain.enum.Category
import app.penny.domain.enum.Currency
import app.penny.domain.enum.TransactionType
import com.ionspin.kotlin.bignum.decimal.BigDecimal

data class TransactionModel(
    val ledgerId: Long = 0,
    val transactionDate: Long = 0,
    val category: Category = Category.MISCELLANEOUS,
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val amount: BigDecimal = BigDecimal.ZERO,
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



