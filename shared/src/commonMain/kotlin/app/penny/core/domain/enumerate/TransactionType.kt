package app.penny.core.domain.enumerate

import app.penny.shared.SharedRes
import dev.icerock.moko.resources.StringResource

enum class TransactionType(
    val displayName: StringResource
) {
    EXPENSE(
        SharedRes.strings.expense
    ),
    INCOME(
        SharedRes.strings.income
    ),
}
