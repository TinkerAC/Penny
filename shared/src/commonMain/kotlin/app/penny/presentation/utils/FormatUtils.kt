package app.penny.presentation.utils

import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.TransactionModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun formatTransactionTime(timestamp: kotlinx.datetime.Instant): String {
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.year}-${
        localDateTime.monthNumber.toString().padStart(2, '0')
    }-${localDateTime.dayOfMonth.toString().padStart(2, '0')} " +
            "${localDateTime.hour.toString().padStart(2, '0')}:${
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