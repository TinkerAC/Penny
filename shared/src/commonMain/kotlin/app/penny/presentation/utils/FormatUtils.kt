package app.penny.presentation.utils

import androidx.compose.runtime.Composable
import app.penny.core.domain.enumerate.TransactionType
import app.penny.core.domain.model.TransactionModel
import app.penny.shared.SharedRes
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun formatTransactionTime(timestamp: kotlinx.datetime.Instant): String {
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.year}-${
        localDateTime.monthNumber.toString().padStart(2, '0')
    }-${localDateTime.dayOfMonth.toString().padStart(2, '0')} " + "${
        localDateTime.hour.toString().padStart(2, '0')
    }:${
        localDateTime.minute.toString().padStart(2, '0')
    }"
}


fun formatAmount(
    transaction: TransactionModel
): String {
    return when (transaction.transactionType == TransactionType.EXPENSE) {
        true -> "-${transaction.amount.toPlainString()}"
        false -> "+${transaction.amount.toPlainString()}"
    }
}


fun formatBigDecimal(value: BigDecimal): String {
    // Configure decimal mode
    val decimalMode = DecimalMode(
        decimalPrecision = 100,
        roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO,
        scale = 2
    )
    val thousand = BigDecimal.fromInt(1_000)
    val million = BigDecimal.fromInt(1_000_000)
    val billion = BigDecimal.fromInt(1_000_000_000)
    val trillion = BigDecimal.fromLong(1_000_000_000_000)

    return when {
        value.abs() >= trillion -> formatWithSuffix(value, trillion, "T", decimalMode)
        value.abs() >= billion -> formatWithSuffix(value, billion, "B", decimalMode)
        value.abs() >= million -> formatWithSuffix(value, million, "M", decimalMode)
        value.abs() >= thousand -> formatWithSuffix(value, thousand, "k", decimalMode)
        else -> value.toPlainString() // Apply decimal mode for rounding
    }
}

private fun formatWithSuffix(
    value: BigDecimal,
    divisor: BigDecimal,
    suffix: String,
    decimalMode: DecimalMode
): String {
    val formatted = value.divide(divisor, decimalMode) // Use decimalMode for division
    return "${formatted.toPlainString()}$suffix"
}


@Composable
fun getLocalizedMonth(
    month: Int, short: Boolean = false
): String {
    return stringResource(
        when (short) {
            true -> when (month) {
                1 -> SharedRes.strings.month_1_short
                2 -> SharedRes.strings.month_2_short
                3 -> SharedRes.strings.month_3_short
                4 -> SharedRes.strings.month_4_short
                5 -> SharedRes.strings.month_5_short
                6 -> SharedRes.strings.month_6_short
                7 -> SharedRes.strings.month_7_short
                8 -> SharedRes.strings.month_8_short
                9 -> SharedRes.strings.month_9_short
                10 -> SharedRes.strings.month_10_short
                11 -> SharedRes.strings.month_11_short
                12 -> SharedRes.strings.month_12_short
                else -> SharedRes.strings.month_1_short
            }

            false -> when (month) {
                1 -> SharedRes.strings.month_1
                2 -> SharedRes.strings.month_2
                3 -> SharedRes.strings.month_3
                4 -> SharedRes.strings.month_9
                5 -> SharedRes.strings.month_9
                6 -> SharedRes.strings.month_9
                7 -> SharedRes.strings.month_9
                8 -> SharedRes.strings.month_9
                9 -> SharedRes.strings.month_9
                10 -> SharedRes.strings.month_10
                11 -> SharedRes.strings.month_11
                12 -> SharedRes.strings.month_12
                else -> {
                    SharedRes.strings.month_1_short
                }
            }
        }
    )
}

