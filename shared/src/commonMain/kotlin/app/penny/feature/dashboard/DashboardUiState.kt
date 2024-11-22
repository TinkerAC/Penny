package app.penny.feature.dashboard

import app.penny.core.domain.model.TransactionModel

data class DashboardUiState(
    val transactions: List<TransactionModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val addTransactionModalVisible: Boolean = false
)
