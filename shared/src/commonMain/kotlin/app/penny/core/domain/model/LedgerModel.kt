package app.penny.core.domain.model

import app.penny.core.domain.enum.Currency
import app.penny.core.domain.enum.LedgerCover
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class LedgerModel @OptIn(ExperimentalUuidApi::class) constructor(
    val userUuid: Uuid = Uuid.fromLongs(0, 0),
    var uuid: Uuid = Uuid.fromLongs(0, 0),
    val name: String = "",
    val currency: Currency = Currency.USD,
    val cover: LedgerCover = LedgerCover.DEFAULT,
    val description: String? = "",
    val count: Int = 0,
    val balance: BigDecimal = BigDecimal.ZERO,
    val createdAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST
)