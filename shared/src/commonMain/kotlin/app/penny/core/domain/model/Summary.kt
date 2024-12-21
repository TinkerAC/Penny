package app.penny.core.domain.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal

data class Summary(
    val totalIncome: BigDecimal,
    val totalExpense: BigDecimal,
    val totalBalance: BigDecimal,
)