package app.penny.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: Long,
    val ledgerId: Long,
    val transactionDate: Long,
    val categoryName: String,
    val transactionType: String,
    val amount: String,
    val currencyCode: String,
    val remark: String?,
    val screenshotUri: String?,
    val createdAt: Long,
    val updatedAt: Long
)