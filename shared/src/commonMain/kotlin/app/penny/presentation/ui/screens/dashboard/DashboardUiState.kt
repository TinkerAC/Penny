package app.penny.presentation.ui.screens.dashboard

import app.penny.domain.model.TransactionModel

data class DashboardUiState(
    val transactions: List<TransactionModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val addTransactionModalVisible: Boolean = false
)
