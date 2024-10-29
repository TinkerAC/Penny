package app.penny.presentation

import app.penny.domain.model.TransactionModel

data class TransactionUiState(
    val transactions: List<TransactionModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
