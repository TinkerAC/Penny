package app.penny.presentation.ui.screens.newTransaction

import app.penny.domain.model.TransactionModel

data class NewTransactionUiState(
    val transactions: List<TransactionModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
