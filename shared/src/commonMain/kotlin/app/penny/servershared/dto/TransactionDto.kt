package app.penny.servershared.dto

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class TransactionDto(
    val userId: Long,
    val uuid: String,
    var ledgerUuid: String,
    val transactionType: String,
    val transactionDate: Long,
    val categoryName: String,
    val currencyCode: String,
    val amount: String,
    val remark: String?,
    val createdAt: Long,
    val updatedAt: Long,
) : BaseEntityDto() {

    override fun editableFields(): List<Pair<String, String?>> {
        return listOf(
            "transactionType" to transactionType,
            "transactionDate" to transactionDate.toString(),
            "categoryName" to categoryName,
            "amount" to amount,
            "remark" to remark
        )
    }

    override fun completedForAction(): Boolean {
        return transactionType.isNotBlank() && transactionDate > 0 && categoryName.isNotBlank() && amount.isNotBlank()
    }







}


