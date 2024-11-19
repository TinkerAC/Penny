package app.penny.domain.model

import app.penny.domain.enum.Currency
import app.penny.domain.enum.LedgerCover
import com.ionspin.kotlin.bignum.decimal.BigDecimal

data class LedgerModel(
    val id: Long = 0,
    val name: String = "",
    val currency: Currency = Currency.USD,
    val cover: LedgerCover = LedgerCover.DEFAULT,
    val description: String? = "",
    val count: Int = 0,
    val balance: BigDecimal = BigDecimal.ZERO,
)