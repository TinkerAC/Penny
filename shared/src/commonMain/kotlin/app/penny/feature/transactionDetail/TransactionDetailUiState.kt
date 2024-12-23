package app.penny.feature.transactionDetail

import app.penny.core.domain.model.TransactionModel
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class TransactionDetailUiState(
    val transactionModel: TransactionModel = TransactionModel(
    )


)