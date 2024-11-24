package app.penny.servershared.dto

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class TransactionDto(
    val uuid: String,
    val ledgerUuid: String,
    val amount: Long,
    val description: String,
    val transactionTime: Long,
    val createdAt: Long,
    val updatedAt: Long
)