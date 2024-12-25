package app.penny.core.domain.enum

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
