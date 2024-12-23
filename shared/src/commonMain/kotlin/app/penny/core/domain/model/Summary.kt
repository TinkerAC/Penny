package app.penny.core.domain.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal

data class Summary(
    val income: BigDecimal,
    val expense: BigDecimal,
    val balance: BigDecimal,
)