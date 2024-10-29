package app.penny.domain.model

import app.penny.data.model.TransactionType
import com.ionspin.kotlin.bignum.decimal.BigDecimal

data class TransactionModel(
    val ledgerId: Long = 0,
    val transactionDate: Long = 0,
    val categoryId: Long = 0,
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val amount: BigDecimal = BigDecimal.ZERO,
    val currencyCode: String = "",
    val content: String? = null,
    val screenshotUri: String? = null,
    val note: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
)



