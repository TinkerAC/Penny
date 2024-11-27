package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val uuid: String,
    val ledgerUuid: String,
    val transactionType: String,
    val transactionDate: Long,
    val category: String,
    val currencyCode: String,
    val amount: String,
    val remark: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)


