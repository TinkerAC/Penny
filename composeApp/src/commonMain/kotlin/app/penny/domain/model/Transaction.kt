package app.penny.domain.model

import kotlinx.datetime.LocalDateTime

// Domain 层的模型（稍后会详细讨论）
data class Transaction(
    val id: String,
    val amount: Int,
    val date: LocalDateTime,

)