package app.penny.servershared.dto.entityDto

import app.penny.servershared.dto.BaseEntityDto
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val userId: Long,
    val uuid: String,
    val ledgerUuid: String,
    val transactionType: String,
    val transactionDate: Long,
    val categoryName: String,
    val currencyCode: String,
    val amount: String,
    val remark: String? = null,
    val createdAt: Long,
    val updatedAt: Long
): BaseEntityDto()


